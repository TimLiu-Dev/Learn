curl -s -c jar.txt -X POST 127.0.0.1:18055/login -d '{"name":"ana","password":"correct horse"}'
curl -s -b jar.txt -X POST 127.0.0.1:18055/books -d '{"title":"Dune","year":1965}'
TOK=$(curl -s -c jar.txt -X POST 127.0.0.1:18055/login -d '{"name":"ana","password":"correct horse"}' | jq -r .csrfToken)
curl -s -b jar.txt -H "X-CSRF-Token: $TOK" -X POST 127.0.0.1:18055/books -d '{"title":"Dune","year":1965}'
grep sid jar.txt | cut -f1-6
