from __future__ import annotations

from pathlib import Path
from typing import Annotated, Any

import typer

from ..pipeline.config import PipelineArgs, build_pipeline_args, pipeline_defaults
from ..pipeline.pipeline import run_pipeline

cli = typer.Typer(no_args_is_help=True, add_completion=False)

llm_app = typer.Typer(no_args_is_help=True)
adopted_app = typer.Typer(no_args_is_help=True)
coverage_app = typer.Typer(no_args_is_help=True)

cli.add_typer(llm_app, name="llm", help="LLM-related steps")
cli.add_typer(adopted_app, name="adopted", help="Steps that operate on adopted tests")
cli.add_typer(coverage_app, name="coverage", help="Coverage comparison steps")

DEFAULTS = pipeline_defaults()


def _base_args(ctx: typer.Context) -> PipelineArgs:
    try:
        return ctx.obj["base"]
    except Exception as exc:  # pragma: no cover
        raise typer.BadParameter("Internal error: base args not initialized.") from exc


def _run(ctx: typer.Context, step: str, **overrides: Any) -> None:
    base = _base_args(ctx)
    merged = {**base.__dict__, **overrides, "step": step}
    rc = run_pipeline(build_pipeline_args(**merged))
    raise typer.Exit(code=rc)


@cli.callback()
def cli_main(
    ctx: typer.Context,
    tests_inventory_csv: Annotated[Path, typer.Argument(help="Path to tests inventory CSV")],
    cut_to_fatjar_map_csv: Annotated[Path, typer.Argument(help="Path to CUT->fat-jar map CSV")],
    generated_dir: Annotated[Path, typer.Option(help="Generated tests root directory")] = Path(
        str(DEFAULTS["generated_dir"])
    ),
    manual_dir: Annotated[Path, typer.Option(help="Manual tests root directory")] = Path(
        str(DEFAULTS["manual_dir"])
    ),
    repos_dir: Annotated[Path, typer.Option(help="Cloned repositories root directory")] = Path(
        str(DEFAULTS["repos_dir"])
    ),
    libs_cp: Annotated[str, typer.Option(help="Classpath glob/pattern for libs")] = str(DEFAULTS["libs_cp"]),
    jacoco_agent: Annotated[Path, typer.Option(help="Path to JaCoCo agent jar")] = Path(
        str(DEFAULTS["jacoco_agent"])
    ),
    out_dir: Annotated[Path, typer.Option(help="Working directory (temp outputs)")] = Path(
        str(DEFAULTS["out_dir"])
    ),
    build_dir: Annotated[Path, typer.Option(help="Build directory for compilation/run steps")] = Path(
        str(DEFAULTS["build_dir"])
    ),
    mode: Annotated[str, typer.Option(help="Mode: generated|manual|both")] = str(DEFAULTS["mode"]),
    includes: Annotated[str, typer.Option(help="Glob for selecting tests/targets")] = str(
        DEFAULTS["includes"]
    ),
    dep_rounds: Annotated[int, typer.Option(help="Dependency resolution rounds")] = int(
        DEFAULTS["dep_rounds"]
    ),
    tool_jar: Annotated[Path, typer.Option(help="Coverage tool jar")] = Path(
        str(DEFAULTS["tool_jar"])
    ),
    timeout_ms: Annotated[int, typer.Option(help="Per-target timeout in ms")] = int(
        DEFAULTS["timeout_ms"]
    ),
    filter_only_agt_covered: Annotated[bool, typer.Option(help="Filter to AGT-covered targets only")] = bool(
        DEFAULTS["filter_only_agt_covered"]
    ),
    coverage_summary: Annotated[str, typer.Option(help="Coverage summary CSV path override")] = str(
        DEFAULTS["coverage_summary"]
    ),
) -> None:
    ctx.obj = {
        "base": build_pipeline_args(
            tests_inventory_csv=str(tests_inventory_csv),
            cut_to_fatjar_map_csv=str(cut_to_fatjar_map_csv),
            generated_dir=str(generated_dir),
            manual_dir=str(manual_dir),
            repos_dir=str(repos_dir),
            libs_cp=libs_cp,
            jacoco_agent=str(jacoco_agent),
            out_dir=str(out_dir),
            build_dir=str(build_dir),
            mode=mode,
            includes=includes,
            dep_rounds=dep_rounds,
            tool_jar=str(tool_jar),
            timeout_ms=timeout_ms,
            filter_only_agt_covered=filter_only_agt_covered,
            coverage_summary=coverage_summary,
        )
    }


@cli.command(help="Run all pipeline steps end-to-end")
def all(ctx: typer.Context) -> None:
    _run(ctx, "all")


@cli.command(help="Compile selected tests and dependencies")
def compile(ctx: typer.Context) -> None:
    _run(ctx, "compile")


@cli.command(help="Run compiled tests and collect coverage")
def run(ctx: typer.Context) -> None:
    _run(ctx, "run")


@cli.command(help="Run coverage filter for generated/manual tests")
def filter(
    ctx: typer.Context,
    covfilter_jar: Annotated[Path, typer.Option(help="Coverage filter tool jar")] = Path(
        str(DEFAULTS["covfilter_jar"])
    ),
    covfilter_out: Annotated[Path, typer.Option(help="Output directory for covfilter results")] = Path(
        str(DEFAULTS["covfilter_out"])
    ),
    sut_classes_dir: Annotated[str, typer.Option(help="Directory of SUT .class files (optional)")] = str(
        DEFAULTS["sut_classes_dir"]
    ),
) -> None:
    _run(
        ctx,
        "filter",
        do_covfilter=True,
        covfilter_jar=str(covfilter_jar),
        covfilter_out=str(covfilter_out),
        sut_classes_dir=sut_classes_dir,
    )


@cli.command(help="Reduce AGT tests using covfilter output")
def reduce(
    ctx: typer.Context,
    reduced_out: Annotated[Path, typer.Option(help="Output directory for reduced tests")] = Path(
        str(DEFAULTS["reduced_out"])
    ),
    max_tests: Annotated[int, typer.Option("--max-tests", min=1, help="Maximum tests to keep")] = int(
        DEFAULTS["reduce_max_tests"]
    ),
    covfilter_jar: Annotated[Path, typer.Option(help="Coverage filter tool jar")] = Path(
        str(DEFAULTS["covfilter_jar"])
    ),
    covfilter_out: Annotated[Path, typer.Option(help="Covfilter output directory")]= Path(
        str(DEFAULTS["covfilter_out"])
    ),
) -> None:
    _run(
        ctx,
        "reduce",
        reduced_out=str(reduced_out),
        reduce_max_tests=max_tests,
        covfilter_jar=str(covfilter_jar),
        covfilter_out=str(covfilter_out),
    )


@cli.command(help="Send reduced tests to LLM service or output directory")
def send(
    ctx: typer.Context,
    send_script: Annotated[str, typer.Option(help="Script used to send payloads (optional)")] = str(
        DEFAULTS["send_script"]
    ),
    send_api_url: Annotated[str, typer.Option(help="GraphQL API URL")] = str(DEFAULTS["send_api_url"]),
    send_sleep_seconds: Annotated[int, typer.Option(help="Sleep seconds between sends")] = int(
        DEFAULTS["send_sleep_seconds"]
    ),
) -> None:
    _run(
        ctx,
        "send",
        send_script=send_script,
        send_api_url=send_api_url,
        send_sleep_seconds=send_sleep_seconds,
    )


@cli.command(help="Run the agent step")
def agent(
    ctx: typer.Context,
    adopted_dir: Annotated[Path, typer.Option(help="Directory containing adopted tests")] = Path(
        str(DEFAULTS["adopted_dir"])
    ),
    agent_model: Annotated[str, typer.Option(help="Model name for agent (optional)")] = str(
        DEFAULTS["agent_model"]
    ),
    max_context_files: Annotated[int, typer.Option(help="Max context files for agent")] = int(
        DEFAULTS["agent_max_context_files"]
    ),
    max_context_chars: Annotated[int, typer.Option(help="Max context characters for agent")] = int(
        DEFAULTS["agent_max_context_chars"]
    ),
    max_prompt_chars: Annotated[int, typer.Option(help="Max prompt characters for agent")] = int(
        DEFAULTS["agent_max_prompt_chars"]
    ),
) -> None:
    _run(
        ctx,
        "agent",
        adopted_dir=str(adopted_dir),
        agent_model=agent_model,
        agent_max_context_files=max_context_files,
        agent_max_context_chars=max_context_chars,
        agent_max_prompt_chars=max_prompt_chars,
    )


@cli.command(help="Compare results (manual vs auto/reduced)")
def compare(
    ctx: typer.Context,
    compare_root: Annotated[Path, typer.Option(help="Path to comparison/metrics implementation")] = Path(
        str(DEFAULTS["compare_root"])
    ),
    compare_out: Annotated[Path, typer.Option(help="Output directory for comparison results")] = Path(
        str(DEFAULTS["compare_out"])
    ),
    min_tokens: Annotated[int, typer.Option("--min-tokens", min=0, help="Minimum token threshold")] = int(
        DEFAULTS["compare_min_tokens"]
    ),
) -> None:
    _run(
        ctx,
        "compare",
        compare_root=str(compare_root),
        compare_out=str(compare_out),
        compare_min_tokens=min_tokens,
    )


@cli.command(name="pull-request-maker", help="Generate pull request artifacts")
def pull_request_maker(
    ctx: typer.Context,
    send_script: Annotated[str, typer.Option(help="Script used to send PRs (optional)")] = str(
        DEFAULTS["send_script"]
    ),
    send_api_url: Annotated[str, typer.Option(help="GraphQL API URL (optional)")] = str(
        DEFAULTS["send_api_url"]
    ),
    send_sleep_seconds: Annotated[int, typer.Option(help="Sleep seconds between sends")] = int(
        DEFAULTS["send_sleep_seconds"]
    ),
) -> None:
    _run(
        ctx,
        "pull-request-maker",
        send_script=send_script,
        send_api_url=send_api_url,
        send_sleep_seconds=send_sleep_seconds,
    )


@llm_app.command("all", help="Run all LLM-related steps")
def llm_all(
    ctx: typer.Context,
    adopted_dir: Annotated[Path, typer.Option(help="Directory to write LLM outputs")] = Path(
        str(DEFAULTS["adopted_dir"])
    ),
) -> None:
    _run(ctx, "llm-all", adopted_dir=str(adopted_dir))


@llm_app.command("improve", help="Improve AGT tests using LLM")
def llm_improve(
    ctx: typer.Context,
    adopted_dir: Annotated[Path, typer.Option(help="Directory to write improved outputs")] = Path(
        str(DEFAULTS["adopted_dir"])
    ),
) -> None:
    _run(ctx, "llm-agt-improvement", adopted_dir=str(adopted_dir))


@llm_app.command("integrate", help="Integrate AGT with manual tests using LLM")
def llm_integrate(
    ctx: typer.Context,
    adopted_dir: Annotated[Path, typer.Option(help="Directory to write integrated outputs")] = Path(
        str(DEFAULTS["adopted_dir"])
    ),
) -> None:
    _run(ctx, "llm-integration", adopted_dir=str(adopted_dir))


@llm_app.command("integrate-sbs", help="Integration step-by-step")
def llm_integrate_step_by_step(
    ctx: typer.Context,
    adopted_dir: Annotated[Path, typer.Option(help="Directory to write integrated outputs")] = Path(
        str(DEFAULTS["adopted_dir"])
    ),
) -> None:
    _run(ctx, "llm-integration-step-by-step", adopted_dir=str(adopted_dir))


@adopted_app.command("fix", help="Fix adopted tests")
def adopted_fix(
    ctx: typer.Context,
    adopted_dir: Annotated[Path, typer.Option(help="Directory containing adopted tests")] = Path(
        str(DEFAULTS["adopted_dir"])
    ),
) -> None:
    _run(ctx, "adopted-fix", adopted_dir=str(adopted_dir))


@adopted_app.command("comment", help="Comment adopted tests")
def adopted_comment(
    ctx: typer.Context,
    adopted_dir: Annotated[Path, typer.Option(help="Directory containing adopted tests")] = Path(
        str(DEFAULTS["adopted_dir"])
    ),
) -> None:
    _run(ctx, "adopted-comment", adopted_dir=str(adopted_dir))


@adopted_app.command("filter", help="Filter adopted tests with covfilter")
def adopted_filter(
    ctx: typer.Context,
    adopted_dir: Annotated[Path, typer.Option(help="Directory containing adopted tests")] = Path(
        str(DEFAULTS["adopted_dir"])
    ),
    adopted_covfilter_out: Annotated[Path, typer.Option(help="Output directory for adopted covfilter")]= Path(
        str(DEFAULTS["adopted_covfilter_out"])
    ),
    covfilter_jar: Annotated[Path, typer.Option(help="Coverage filter tool jar")] = Path(
        str(DEFAULTS["covfilter_jar"])
    ),
    sut_classes_dir: Annotated[str, typer.Option(help="Directory of SUT .class files (optional)")] = str(
        DEFAULTS["sut_classes_dir"]
    ),
) -> None:
    _run(
        ctx,
        "adopted-filter",
        adopted_dir=str(adopted_dir),
        adopted_covfilter_out=str(adopted_covfilter_out),
        covfilter_jar=str(covfilter_jar),
        sut_classes_dir=sut_classes_dir,
        do_covfilter=True,
    )


@adopted_app.command("reduce", help="Reduce adopted tests")
def adopted_reduce(
    ctx: typer.Context,
    adopted_dir: Annotated[Path, typer.Option(help="Directory containing adopted tests")] = Path(
        str(DEFAULTS["adopted_dir"])
    ),
    adopted_reduced_out: Annotated[Path, typer.Option(help="Output directory for reduced adopted tests")]= Path(
        str(DEFAULTS["adopted_reduced_out"])
    ),
    adopted_covfilter_out: Annotated[Path, typer.Option(help="Covfilter output for adopted tests")]= Path(
        str(DEFAULTS["adopted_covfilter_out"])
    ),
    max_tests: Annotated[int, typer.Option("--max-tests", min=1, help="Maximum adopted tests to keep")] = int(
        DEFAULTS["adopted_reduce_max_tests"]
    ),
    covfilter_jar: Annotated[Path, typer.Option(help="Coverage filter tool jar")] = Path(
        str(DEFAULTS["covfilter_jar"])
    ),
) -> None:
    _run(
        ctx,
        "adopted-reduce",
        adopted_dir=str(adopted_dir),
        adopted_reduced_out=str(adopted_reduced_out),
        adopted_covfilter_out=str(adopted_covfilter_out),
        adopted_reduce_max_tests=max_tests,
        covfilter_jar=str(covfilter_jar),
    )


@adopted_app.command("run", help="Run adopted tests")
def adopted_run(
    ctx: typer.Context,
    adopted_dir: Annotated[Path, typer.Option(help="Directory containing adopted tests")] = Path(
        str(DEFAULTS["adopted_dir"])
    ),
) -> None:
    _run(ctx, "adopted-run", adopted_dir=str(adopted_dir))


@coverage_app.command("compare", help="Coverage comparison (manual/auto/adopted)")
def coverage_compare(ctx: typer.Context) -> None:
    _run(ctx, "coverage-comparison")


@coverage_app.command("compare-reduced", help="Coverage comparison for reduced sets")
def coverage_compare_reduced(
    ctx: typer.Context,
    top_n: Annotated[int, typer.Option("--top-n", min=1, help="Top-N reduced tests to compare")]= int(
        DEFAULTS["coverage_compare_top_n"]
    ),
    reduced_out: Annotated[Path, typer.Option(help="Reduced tests output directory")]= Path(
        str(DEFAULTS["reduced_out"])
    ),
    adopted_reduced_out: Annotated[Path, typer.Option(help="Reduced adopted tests output directory")]= Path(
        str(DEFAULTS["adopted_reduced_out"])
    ),
) -> None:
    _run(
        ctx,
        "coverage-comparison-reduced",
        coverage_compare_top_n=top_n,
        reduced_out=str(reduced_out),
        adopted_reduced_out=str(adopted_reduced_out),
    )


if __name__ == "__main__":
    cli()
