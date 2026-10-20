"""Shared helpers for memviz step specs (imported by build.py and ../viz/*.py)."""


def S(kt, jv, note, stack, heap=()):
    return {"kt": kt, "jv": jv, "note": note,
            "stack": [{"name": n, "vars": [list(v) for v in vs]} for n, vs in stack],
            "heap": [{"id": i, "type": t, "fields": [list(f) for f in fs]} for i, t, fs in heap]}


D = "—"  # not assigned yet
