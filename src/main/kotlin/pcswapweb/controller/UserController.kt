package pcswapweb.controller

import com.fasterxml.jackson.core.type.TypeReference
import org.apache.log4j.Logger
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import pcswapobjects.request.RequestBase
import pcswapobjects.request.user.*
import pcswapobjects.response.ResponseBase
import pcswapobjects.response.user.*
import pcswapobjects.user.User
import pcswapweb.service.post
import pcswapweb.service.typeRef
import javax.servlet.http.HttpSession

@Controller
class UserController {

    val log = Logger.getLogger(UserController::class.java)

    @GetMapping("/createUser")
    fun createUser(httpSession: HttpSession, model: Model): String {
        updateSession(httpSession, model)

        return "createuser"
    }

    @PostMapping("/createUser")
    fun createUser(email: String, username: String, password: String, confirmPassword: String, httpSession: HttpSession, model: Model): String {
        var request = CreateUserRequest(username, email, password)
        var requestBase = RequestBase<CreateUserRequest>(payload=request, sessionId="")

        post("http://localhost:2222/createUser", requestBase, typeRef<ResponseBase<CreateUserResponse>>())

        updateSession(httpSession, model)

        return "redirect:index.html"
    }

    @GetMapping("/login")
    fun login(httpSession: HttpSession, model: Model): String {
        updateSession(httpSession, model)

        return "login"
    }

    @PostMapping("/login")
    fun login(email: String, password: String, httpSession: HttpSession, model: Model): String {
        var request = UserLoginRequest(email, password)
        var requestBase = RequestBase<UserLoginRequest>(payload=request, sessionId="")

        var response = post("http://localhost:2222/userLogin", requestBase, typeRef<ResponseBase<UserLoginResponse>>())
        if(response != null) {
            var payload = response.payload
            if(payload != null) {
                httpSession.setAttribute("sessionId", payload.sessionId)
            }
        }

        httpSession.setAttribute("signedIn", "true")
        updateSession(httpSession, model)

        return "redirect:index.html"
    }

    @GetMapping("/profile")
    fun viewUser(httpSession: HttpSession, model: Model): String {
        model.addAttribute("user", getUserFromSessionId(httpSession.getAttribute("sessionId") as String))

        updateSession(httpSession, model)

        return "viewuser"
    }

    @GetMapping("/logout")
    fun logout(httpSession: HttpSession, model: Model): String {
        var sessionId = httpSession.getAttribute("sessionId") as String
        var request = RequestBase<LogoutRequest>(sessionId=sessionId, payload= LogoutRequest(sessionId=sessionId))
        post("http://localhost:2222/logout", request, typeRef<ResponseBase<LogoutResponse>>())

        httpSession.removeAttribute("sessionId")
        httpSession.setAttribute("signedIn", "false")
        updateSession(httpSession, model)

        return "redirect:index.html"
    }

    fun getUserFromSessionId(sessionId: String): User? {
        var user: User? = null

        var getSessionRequest = RequestBase<GetUserSessionRequest>(sessionId=sessionId, payload=GetUserSessionRequest(sessionId))
        var getSessionResponse = post("http://localhost:2222/getUserSession", getSessionRequest, typeRef<ResponseBase<GetUserSessionResponse>>())
        if(getSessionResponse != null) {
            var getSessionPayload = getSessionResponse.payload
            if(getSessionPayload != null) {
                var session = getSessionPayload.session
                if(session != null) {
                    var getUserRequest = RequestBase<GetUserRequest>(sessionId = "", payload = GetUserRequest(session.userId))
                    var getUserResponse = post("http://localhost:2222/getUser", getUserRequest, typeRef<ResponseBase<GetUserResponse>>())
                    if(getUserResponse != null) {
                        var getUserPayload = getUserResponse.payload
                        if(getUserPayload != null) {
                            user = getUserPayload.user
                        }
                    }
                }
            }
        }

        return user
    }
}