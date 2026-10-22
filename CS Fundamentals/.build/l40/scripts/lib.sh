# Shared helpers for the L40 throwaway-repo transcripts.
# Fixed identity and dates (local to each repo/command) so commit hashes are reproducible.
export GIT_AUTHOR_NAME="Sam Learner" GIT_AUTHOR_EMAIL="sam@example.com"
export GIT_COMMITTER_NAME="Sam Learner" GIT_COMMITTER_EMAIL="sam@example.com"
export GIT_CONFIG_GLOBAL=/dev/null GIT_CONFIG_NOSYSTEM=1   # ignore any global/system git config
TICK=1792576000
tick() { TICK=$((TICK + 60)); export GIT_AUTHOR_DATE="@$TICK +0000" GIT_COMMITTER_DATE="@$TICK +0000"; }
newrepo() { rm -rf "$1"; mkdir -p "$1"; cd "$1"; git init -q -b main; git config core.pager cat; }
show() { printf '$ %s\n' "$*"; eval "$@"; }        # print the command, then run it
