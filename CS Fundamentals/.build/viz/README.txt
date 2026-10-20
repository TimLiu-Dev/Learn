One file per lesson, e.g. l6.py:
    import sys, pathlib; sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent.parent / "bin"))
    from vizlib import S, D
    VIZ = {"l6_account": {"kotlin": "l6/Account.kt", "java": "l6/Account.java", "steps": [S(...), ...]}}
Viz names must be globally unique and match \w+ (letters, digits, underscore), so prefix them like "l6_".
