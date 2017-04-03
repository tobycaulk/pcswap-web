package pcswapweb.model

import pcswapobjects.swap.SwapItem
import pcswapobjects.user.User

data class SwapListing(var sellItem: SwapItem, var tradeForItem: SwapItem, var seller: User)