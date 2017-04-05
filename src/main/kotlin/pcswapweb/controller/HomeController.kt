package pcswapweb.controller

import org.apache.log4j.Logger
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import pcswapobjects.request.RequestBase
import pcswapobjects.request.swap.GetRecentSwapListingsRequest
import pcswapobjects.request.swap.GetSwapItemRequest
import pcswapobjects.request.swap.GetTotalSwapCountRequest
import pcswapobjects.request.user.GetUserRequest
import pcswapobjects.response.ResponseBase
import pcswapobjects.response.swap.GetRecentSwapListingsResponse
import pcswapobjects.response.swap.GetSwapItemResponse
import pcswapobjects.response.swap.GetTotalSwapCountResponse
import pcswapobjects.response.user.GetUserResponse
import pcswapobjects.swap.SwapItem
import pcswapobjects.user.User
import pcswapweb.model.SwapListing
import pcswapweb.service.post
import pcswapweb.service.typeRef
import javax.servlet.http.HttpSession

@Controller
class HomeController {

    val log = Logger.getLogger(HomeController::class.java)

    @GetMapping("/")
    fun index(model: Model, httpSession: HttpSession): String {
        model.addAttribute("totalSwapCount", getTotalSwapCount())
        model.addAttribute("recentSwapListings", getRecentSwapListings())

        updateSession(httpSession, model)

        return "index"
    }

    fun getRecentSwapListings(): MutableList<SwapListing> {
        var swapListings = mutableListOf<SwapListing>()

        var request = GetRecentSwapListingsRequest()
        var requestBase = RequestBase<GetRecentSwapListingsRequest>(sessionId="", payload=request)

        var response = post("http://localhost:2222/getRecentSwapListings", requestBase, typeRef<ResponseBase<GetRecentSwapListingsResponse>>())
        if(response != null) {
            var payload = response.payload
            if(payload != null) {
                var recentSwapListings = payload.swapListings
                for(swap in recentSwapListings!!) {
                    var sellItem = getSwapItem(swap.sellItem)
                    if(sellItem != null) {
                        var user = getUser(sellItem.userId!!)
                        if(user != null) {
                            var tradeForItem = getSwapItem(swap.tradeForItems[0])
                            if (tradeForItem != null) {
                                swapListings.add(SwapListing(sellItem, tradeForItem, user, swap.createDate))
                            }
                        }
                    }
                }
            }
        }

        return swapListings
    }

    fun getUser(userId: String): User? {
        var user: User? = null

        var request = GetUserRequest(userId)
        var requestBase = RequestBase<GetUserRequest>(payload=request, sessionId="")

        var response = post("http://localhost:2222/getUser", requestBase, typeRef<ResponseBase<GetUserResponse>>())
        if(response != null) {
            var payload = response.payload
            if(payload != null) {
                user = payload.user
            }
        }

        return user
    }

    fun getSwapItem(swapItemId: String): SwapItem? {
        var swapItem: SwapItem? = null

        var request = GetSwapItemRequest(swapItemId)
        var requestBase = RequestBase<GetSwapItemRequest>(payload=request, sessionId="")

        var response = post("http://localhost:2222/getSwapItem", requestBase, typeRef<ResponseBase<GetSwapItemResponse>>())
        if(response != null) {
            var payload = response.payload
            if(payload != null) {
                swapItem = payload.swapItem
            }
        }

        return swapItem
    }

    fun getTotalSwapCount(): Int {
        var request = GetTotalSwapCountRequest()
        var requestBase = RequestBase<GetTotalSwapCountRequest>(sessionId="", payload=request)

        var response = post("http://localhost:2222/getTotalSwapCount", requestBase, typeRef<ResponseBase<GetTotalSwapCountResponse>>())
        if(response != null) {
            var payload = response.payload
            if(payload != null) {
                return payload.totalSwapCount * 2
            }
        }

        return 0
    }
}