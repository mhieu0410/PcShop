package pcshop.duancanhan.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pcshop.duancanhan.pojo.Cart;
import pcshop.duancanhan.service.CartService;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String viewCart(Model model) {
        Cart cart = cartService.getCartByCustomer();
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", cartService.getTotalPrice(cart));
        model.addAttribute("pageTitle", "Giỏ hàng");
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        try {
            cartService.addToCart(productId, quantity);
        } catch (RuntimeException e) {
            return "redirect:/products/" + productId + "?error=" + e.getMessage();
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long cartItemId, @RequestParam int quantity) {
        try {
            cartService.updateCartItem(cartItemId, quantity);
        } catch (RuntimeException e) {
            return "redirect:/cart?error=" + e.getMessage();
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeCartItem(@RequestParam Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return "redirect:/cart";
    }

    @GetMapping("/add/{id}")
    @ResponseBody
    public ResponseEntity<String> addToCart(@PathVariable("id") Long productId, HttpSession session) {
        cartService.addToCart(productId, session);
        return ResponseEntity.ok("Added to cart");
    }

}