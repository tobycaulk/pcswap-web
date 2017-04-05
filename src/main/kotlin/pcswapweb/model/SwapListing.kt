package pcswapweb.model

import pcswapobjects.swap.SwapItem
import pcswapobjects.user.User
import java.util.*

data class SwapListing(var sellItem: SwapItem, var tradeForItem: SwapItem, var seller: User, var createDate: Date)