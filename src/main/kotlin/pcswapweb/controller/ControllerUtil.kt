package pcswapweb.controller

import org.springframework.ui.Model
import javax.servlet.http.HttpSession

fun updateSession(httpSession: HttpSession, model: Model): Model {
    if(httpSession.getAttribute("signedIn") == "true") {
        model.addAttribute("signedIn", "true")
    } else {
        model.addAttribute("signedIn", "false")
    }

    return model
}