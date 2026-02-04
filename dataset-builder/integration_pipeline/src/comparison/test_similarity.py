from __future__ import annotations

import math
from dataclasses import dataclass
from typing import Any, Dict, Optional

import numpy as np


# -----------------------------
# Utilities
# -----------------------------
def cosine_similarity(a: np.ndarray, b: np.ndarray) -> float:
    a = a.astype(np.float64)
    b = b.astype(np.float64)
    na = np.linalg.norm(a)
    nb = np.linalg.norm(b)
    if na == 0.0 or nb == 0.0:
        return 0.0
    return float(np.dot(a, b) / (na * nb))


@dataclass
class SimilarityResult:
    codebleu: Optional[float]
    ast_ted: Optional[float]            # raw distance (lower = closer)
    ast_ted_norm: Optional[float]       # normalized to [0,1] where 1 = closer
    embedding_cosine: Optional[float]
    details: Dict[str, Any]


# -----------------------------
# 1) CodeBLEU (optional)
# -----------------------------
def codebleu_score(code_a: str, code_b: str, lang: str = "java") -> Optional[float]:
    """
    Returns CodeBLEU score in [0,1] if available, else None.
    Uses the unofficial `codebleu` PyPI package.
    """
    try:
        # k4black/codebleu package
        from codebleu import calc_codebleu  # type: ignore
    except Exception:
        return None

    # API shape may differ across versions; handle common patterns.
    # Many versions use: calc_codebleu(references=[...], predictions=[...], lang="java")
    try:
        out = calc_codebleu(references=[code_b], predictions=[code_a], lang=lang)
        # common return: {"codebleu": float, ...}
        if isinstance(out, dict):
            return float(out.get("codebleu"))
        return float(out)
    except TypeError:
        # alternate signature: calc_codebleu([preds], [refs], lang)
        try:
            out = calc_codebleu([code_a], [code_b], lang)
            if isinstance(out, dict):
                return float(out.get("codebleu"))
            return float(out)
        except Exception:
            return None
    except Exception:
        return None


# -----------------------------
# 2) AST Tree Edit Distance (optional)
# -----------------------------
def _python_ast_to_zss_node(py_ast_node) -> "Node":
    """
    Convert Python ast.AST into a zss.Node tree.
    Only works for Python code snippets.
    """
    import ast
    from zss import Node  # type: ignore

    def label(n: ast.AST) -> str:
        return type(n).__name__

    def children(n: ast.AST):
        ch = []
        for _field, value in ast.iter_fields(n):
            if isinstance(value, ast.AST):
                ch.append(value)
            elif isinstance(value, list):
                for item in value:
                    if isinstance(item, ast.AST):
                        ch.append(item)
        return ch

    root = Node(label(py_ast_node))
    stack = [(py_ast_node, root)]
    while stack:
        src, dst = stack.pop()
        for c in children(src):
            cn = Node(label(c))
            dst.addkid(cn)
            stack.append((c, cn))
    return root


def ast_tree_edit_distance_python(code_a: str, code_b: str) -> Optional[Dict[str, float]]:
    """
    Computes Zhang–Shasha TED between Python ASTs.
    Returns dict with raw distance + normalized similarity in [0,1].
    """
    try:
        import ast
        from zss import simple_distance  # type: ignore
    except Exception:
        return None

    try:
        a_ast = ast.parse(code_a)
        b_ast = ast.parse(code_b)
    except SyntaxError:
        return None

    a_tree = _python_ast_to_zss_node(a_ast)
    b_tree = _python_ast_to_zss_node(b_ast)

    dist = float(simple_distance(a_tree, b_tree))
    # Normalize: similarity = 1 - dist / max(nodes_a, nodes_b, 1)
    # (crude but works well enough for a stable [0,1] score)
    def count_nodes(node) -> int:
        n = 1
        for c in node.children:
            n += count_nodes(c)
        return n

    na = count_nodes(a_tree)
    nb = count_nodes(b_tree)
    denom = float(max(na, nb, 1))
    sim = 1.0 - min(dist / denom, 1.0)

    return {"ast_ted": dist, "ast_ted_norm": sim, "nodes_a": float(na), "nodes_b": float(nb)}


# -----------------------------
# 3) Embedding similarity (optional) using CodeBERT
# -----------------------------
def codebert_embedding(code: str, model_name: str = "microsoft/codebert-base") -> Optional[np.ndarray]:
    """
    Produces a single vector embedding using mean pooling over last hidden states.
    """
    try:
        import torch
        from transformers import AutoTokenizer, AutoModel
    except Exception:
        return None

    tokenizer = AutoTokenizer.from_pretrained(model_name)
    model = AutoModel.from_pretrained(model_name)
    model.eval()

    with torch.no_grad():
        inputs = tokenizer(
            code,
            return_tensors="pt",
            truncation=True,
            max_length=512,
        )
        out = model(**inputs)
        last_hidden = out.last_hidden_state  # [1, seq, dim]
        attn = inputs["attention_mask"].unsqueeze(-1).float()  # [1, seq, 1]
        masked = last_hidden * attn
        summed = masked.sum(dim=1)
        counts = attn.sum(dim=1).clamp(min=1.0)
        mean = summed / counts  # [1, dim]
        vec = mean.squeeze(0).cpu().numpy()
        return vec.astype(np.float32)


# -----------------------------
# Main compare function
# -----------------------------
def compare_tests(
    code_a: str,
    code_b: str,
    *,
    lang_for_codebleu: str = "java",
    compute_codebleu: bool = True,
    compute_ast_ted_python: bool = False,
    compute_embeddings: bool = True,
) -> SimilarityResult:
    details: Dict[str, Any] = {}

    cb = codebleu_score(code_a, code_b, lang=lang_for_codebleu) if compute_codebleu else None
    if compute_codebleu:
        details["codebleu_lang"] = lang_for_codebleu

    ted = None
    ted_norm = None
    if compute_ast_ted_python:
        ted_out = ast_tree_edit_distance_python(code_a, code_b)
        if ted_out is not None:
            ted = float(ted_out["ast_ted"])
            ted_norm = float(ted_out["ast_ted_norm"])
            details["ast_nodes_a"] = int(ted_out["nodes_a"])
            details["ast_nodes_b"] = int(ted_out["nodes_b"])

    emb_cos = None
    if compute_embeddings:
        va = codebert_embedding(code_a)
        vb = codebert_embedding(code_b)
        if va is not None and vb is not None:
            emb_cos = cosine_similarity(va, vb)

    return SimilarityResult(
        codebleu=cb,
        ast_ted=ted,
        ast_ted_norm=ted_norm,
        embedding_cosine=emb_cos,
        details=details,
    )


if __name__ == "__main__":
    # Minimal example:
    a = "public void testA(){ assertEquals(1, 1); }"
    b = "public void testB(){ org.junit.Assert.assertEquals(1, 1); }"

    res = compare_tests(
        a,
        b,
        lang_for_codebleu="java",
        compute_codebleu=True,
        compute_ast_ted_python=False,   # set True only if the code is Python
        compute_embeddings=True,
    )
    print(res)