package pcswapweb.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import pcswapobjects.request.RequestBase
import pcswapobjects.request.swap.CreateSwapRequest
import pcswapobjects.request.swap.GetSwapItemRequest
import pcswapobjects.request.swap.GetSwapRequest
import pcswapobjects.request.user.GetUserRequest
import pcswapobjects.response.ResponseBase
import pcswapobjects.response.swap.CreateSwapResponse
import pcswapobjects.response.swap.GetSwapItemResponse
import pcswapobjects.response.swap.GetSwapResponse
import pcswapobjects.response.user.GetUserResponse
import pcswapobjects.swap.SwapItem
import pcswapobjects.user.User
import pcswapweb.service.post
import pcswapweb.service.typeRef
import javax.servlet.http.HttpSession

@Controller
class SwapController {

    @GetMapping("/createSwap")
    fun createSwap(httpSession: HttpSession, model: Model): String {
        updateSession(httpSession, model)

        return "createswap"
    }

    @PostMapping("/createSwap")
    fun createSwap(sellItem: String, sellItemDescription: String, sellItemImageUrl: String, tradeForItem: String, httpSession: HttpSession, model: Model): String {
        var sellItem = SwapItem(
                swapItemId=null,
                userId=null,
                item=sellItem,
                description=sellItemDescription,
                imageUrl=sellItemImageUrl)

        var tradeItem = SwapItem(
                swapItemId=null,
                userId=null,
                item=tradeForItem,
                description=null,
                imageUrl=null)

        var request = CreateSwapRequest(sellItem, listOf(tradeItem))
        var requestBase = RequestBase<CreateSwapRequest>(payload=request, sessionId=httpSession.getAttribute("sessionId") as String)

        post("http://localhost:2222/createSwap", requestBase, typeRef<ResponseBase<CreateSwapResponse>>())

        updateSession(httpSession, model)

        return "redirect:/"
    }

    @GetMapping("/viewSwap")
    fun viewSwap(swapId: String, httpSession: HttpSession, model: Model): String {
        var request = GetSwapRequest(swapId)
        var requestBase = RequestBase<GetSwapRequest>(payload=request, sessionId=httpSession.getAttribute("sessionId") as String)

        var response = post("http://localhost:2222/getSwap", requestBase, typeRef<ResponseBase<GetSwapResponse>>())
        if(response != null) {
            var payload = response.payload
            if(payload != null) {
                var swap = payload.swap
                if(swap != null) {
                    model.addAttribute("swap", swap)
                    var sellItem = getSwapItem(swap.sellItem)
                    if(sellItem != null) {
                        model.addAttribute("sellItem", sellItem)
                        var seller = getUser(sellItem.userId!!)
                        if(seller != null) {
                            model.addAttribute("seller", seller)
                        }
                    }

                    var tradeForItem = getSwapItem(swap.tradeForItems.get(0))
                    if(tradeForItem != null) {
                        model.addAttribute("tradeForItem", tradeForItem)
                    }
                }
            }
        }

        updateSession(httpSession, model)

        return "viewswap"
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
}