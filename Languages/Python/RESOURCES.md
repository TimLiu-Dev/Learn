# Python Engineering (2027) Resources

## Knowledge

- [Official Python 3 Documentation (Library, Language Reference, Data Model)](https://docs.python.org/3/)
  Authoritative primary specification for Python syntax, built-in types, `dis`, `decimal`, `asyncio`, and data model dunders. Use for: verifying exact semantics and edge cases in every lesson.
- [Ned Batchelder: "Facts and Myths about Python Names and Values"](https://nedbatchelder.com/text/names1.html)
  The clearest mental-model talk/article on Python variables as name bindings, aliasing, mutability, and function argument passing. Use for: Lesson 01 and Phase 1–2 memory reasoning.
- [roadmap.sh — Python Developer Roadmap](https://roadmap.sh/python)
  Industry baseline topic map. Use for: auditing coverage across core Python, DSA, tooling, concurrency, and web frameworks.
- [Astral `uv` & `ruff` Documentation](https://docs.astral.sh/)
  Primary reference for modern Python 2027 dependency resolution, `pyproject.toml` workspaces, linting, and formatting. Use for: Lesson 09, Lesson 12, and Capstone setup.
- [Pydantic v2 Documentation](https://docs.pydantic.dev/latest/)
  Canonical guide to runtime validation, strict typing, settings management, and serialization. Use for: Lesson 10, Lesson 16, and Capstone.
- [FastAPI Official Documentation](https://fastapi.tiangolo.com/)
  Primary reference for ASGI web APIs, dependency injection, OpenAPI contracts, and async endpoints. Use for: Lesson 16 and Capstone.
- [SQLAlchemy 2.0 Documentation](https://docs.sqlalchemy.org/en/20/)
  Authoritative guide to typed ORM models, async sessions, transaction boundaries, and connection pooling. Use for: Lesson 17 and Capstone.
- [Book: _Fluent Python_ (2nd ed.) — Luciano Ramalho](https://www.oreilly.com/library/view/fluent-python-2nd/9781492056348/)
  Deep dive into Python's data model, iterators, decorators, protocols, and concurrency. Use for: Phase 2 and Phase 4 senior-level depth.

## Wisdom (Communities)

- [Python Discourse (`discuss.python.org`)](https://discuss.python.org/)
  Official core Python community forum (PEPs, packaging, typing, help). Use for: staying current on Python 3.13–3.15 direction and nuanced language questions.
- [PyCon US Video Archive (`pyvideo.org` / YouTube)](https://pyvideo.org/)
  High-signal talks by core developers and production engineers. Use for: real-world war stories on performance, testing, and architecture.
- [r/Python](https://www.reddit.com/r/Python/) & [Real Python Discord / PySlackers](https://pyslackers.com/)
  Practitioner communities for code review, tooling debates, and career feedback. Use for: sharing your Capstone repo for external critique.

## Gaps
- Need to verify a concise primary benchmark reference for Python 3.13/3.14 Free-Threading (PEP 703) compatibility list when authoring Lesson 13.
