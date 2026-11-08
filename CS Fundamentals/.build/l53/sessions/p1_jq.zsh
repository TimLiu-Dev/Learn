cat books.json
jq '.[0]' books.json
jq -r '.[].title' books.json
jq -r '.[] | select(.year < 1950) | "\(.year) \(.title)"' books.json
jq 'map(.pages) | add' books.json
jq -c 'group_by(.author) | map({author: .[0].author, books: length})' books.json
jq -r '.[] | [.year, .title] | @tsv' books.json | sort -n | head -2
