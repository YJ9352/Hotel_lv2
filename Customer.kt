package hotel.lv2

data class Customer (
    val name: String,
    val money: BankAccount = BankAccount()
)

