from __future__ import annotations

import argparse
from pathlib import Path

import typer

from ..pipeline.pipeline import PipelineArgs, run_pipeline


cli = typer.Typer(no_args_is_help=True, add_completion=False)


def _build_args(ctx: typer.Context, step: str) -> PipelineArgs:
    base = dict(ctx.obj or {})
    return base["args"].__class__(**{**base["args"].__dict__, "step": step})


@cli.callback()
def cli_main(
    ctx: typer.Context,
    tests_inventory_csv: Path = typer.Argument(...),
    cut_to_fatjar_map_csv: Path = typer.Argument(...),
    generated_dir: Path = typer.Option(Path("../generated")),
    manual_dir: Path = typer.Option(Path("../manual")),
    repos_dir: Path = typer.Option(Path("../repos")),
    libs_cp: str = typer.Option("libs/*"),
    jacoco_agent: Path = typer.Option(Path("jacoco-deps/org.jacoco.agent-run-0.8.14.jar")),
    out_dir: Path = typer.Option(Path("tmp")),
    build_dir: Path = typer.Option(Path("build/agt")),
    mode: str = typer.Option("both"),
    includes: str = typer.Option("*"),
    dep_rounds: int = typer.Option(3),
    tool_jar: Path = typer.Option(Path("coverage-filter-1.0-SNAPSHOT.jar")),
    timeout_ms: int = typer.Option(240_000),
    filter_only_agt_covered: bool = typer.Option(False),
    coverage_summary: str = typer.Option(""),
    reduced_out: Path = typer.Option(Path("results/reduced-agt")),
    reduce_max_tests: int = typer.Option(100),
    send_script: str = typer.Option(""),
    send_api_url: str = typer.Option("http://localhost:8001/graphql"),
    send_sleep_seconds: int = typer.Option(30),
    adopted_dir: Path = typer.Option(Path("results/llm-out")),
    agent_model: str = typer.Option(""),
    agent_max_context_files: int = typer.Option(12),
    agent_max_context_chars: int = typer.Option(40_000),
    agent_max_prompt_chars: int = typer.Option(60_000),
    compare_root: Path = typer.Option(Path("src/metrics")),
    compare_out: Path = typer.Option(Path("results/compare")),
    compare_min_tokens: int = typer.Option(50),
    coverage_compare_top_n: int = typer.Option(5),
    do_covfilter: bool = typer.Option(False),
    covfilter_jar: str = typer.Option(""),
    covfilter_out: Path = typer.Option(Path("results/covfilter")),
    sut_classes_dir: str = typer.Option(""),
    adopted_covfilter_out: Path = typer.Option(Path("results/covfilter-adopted")),
    adopted_reduced_out: Path = typer.Option(Path("results/reduced-adopted")),
    adopted_reduce_max_tests: int = typer.Option(5),
) -> None:
    ctx.obj = {
        "args": PipelineArgs(
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
            reduced_out=str(reduced_out),
            reduce_max_tests=reduce_max_tests,
            send_script=send_script,
            send_api_url=send_api_url,
            send_sleep_seconds=send_sleep_seconds,
            adopted_dir=str(adopted_dir),
            agent_model=agent_model,
            agent_max_context_files=agent_max_context_files,
            agent_max_context_chars=agent_max_context_chars,
            agent_max_prompt_chars=agent_max_prompt_chars,
            compare_root=str(compare_root),
            compare_out=str(compare_out),
            compare_min_tokens=compare_min_tokens,
            coverage_compare_top_n=coverage_compare_top_n,
            do_covfilter=do_covfilter,
            covfilter_jar=covfilter_jar,
            covfilter_out=str(covfilter_out),
            sut_classes_dir=sut_classes_dir,
            adopted_covfilter_out=str(adopted_covfilter_out),
            adopted_reduced_out=str(adopted_reduced_out),
            adopted_reduce_max_tests=adopted_reduce_max_tests,
        )
    }


def _run_step(ctx: typer.Context, step: str) -> None:
    args = _build_args(ctx, step)
    code = run_pipeline(args)
    raise typer.Exit(code=code)


@cli.command()
def all(ctx: typer.Context) -> None:
    _run_step(ctx, "all")


@cli.command()
def compile(ctx: typer.Context) -> None:
    _run_step(ctx, "compile")


@cli.command()
def run(ctx: typer.Context) -> None:
    _run_step(ctx, "run")


@cli.command()
def filter(ctx: typer.Context) -> None:
    _run_step(ctx, "filter")


@cli.command()
def reduce(ctx: typer.Context) -> None:
    _run_step(ctx, "reduce")


@cli.command(name="llm-all")
def llm_all(ctx: typer.Context) -> None:
    _run_step(ctx, "llm-all")


@cli.command(name="llm-agt-improvement")
def llm_agt_improvement(ctx: typer.Context) -> None:
    _run_step(ctx, "llm-agt-improvement")


@cli.command(name="llm-integration")
def llm_integration(ctx: typer.Context) -> None:
    _run_step(ctx, "llm-integration")


@cli.command(name="llm-integration-step-by-step")
def llm_integration_step_by_step(ctx: typer.Context) -> None:
    _run_step(ctx, "llm-integration-step-by-step")


@cli.command()
def agent(ctx: typer.Context) -> None:
    _run_step(ctx, "agent")


@cli.command()
def compare(ctx: typer.Context) -> None:
    _run_step(ctx, "compare")


@cli.command(name="adopted-fix")
def adopted_fix(ctx: typer.Context) -> None:
    _run_step(ctx, "adopted-fix")


@cli.command(name="adopted-comment")
def adopted_comment(ctx: typer.Context) -> None:
    _run_step(ctx, "adopted-comment")


@cli.command(name="adopted-filter")
def adopted_filter(ctx: typer.Context) -> None:
    _run_step(ctx, "adopted-filter")


@cli.command(name="adopted-reduce")
def adopted_reduce(ctx: typer.Context) -> None:
    _run_step(ctx, "adopted-reduce")


@cli.command(name="adopted-run")
def adopted_run(ctx: typer.Context) -> None:
    _run_step(ctx, "adopted-run")


@cli.command(name="pull-request-maker")
def pull_request_maker(ctx: typer.Context) -> None:
    _run_step(ctx, "pull-request-maker")


@cli.command(name="coverage-comparison")
def coverage_comparison(ctx: typer.Context) -> None:
    _run_step(ctx, "coverage-comparison")


@cli.command(name="coverage-comparison-reduced")
def coverage_comparison_reduced(ctx: typer.Context) -> None:
    _run_step(ctx, "coverage-comparison-reduced")


if __name__ == "__main__":
    cli()
