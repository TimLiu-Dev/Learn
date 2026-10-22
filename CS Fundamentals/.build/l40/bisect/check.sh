#!/bin/sh
# Exit 0 if this commit is good, 1 if bad: what "git bisect run" expects.
java Checkout.java | grep -q "total: 2800 cents"
