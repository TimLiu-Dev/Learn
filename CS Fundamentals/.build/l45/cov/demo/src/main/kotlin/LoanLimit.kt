const val MAX_OPEN_LOANS = 3

// May a member who already holds `openLoans` open loans borrow one more?
fun mayBorrow(openLoans: Int): Boolean = openLoans <= MAX_OPEN_LOANS
