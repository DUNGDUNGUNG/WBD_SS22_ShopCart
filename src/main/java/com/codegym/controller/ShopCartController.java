package com.codegym.controller;

import com.codegym.model.Item;
import com.codegym.model.Product;
import com.codegym.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class ShopCartController {

    @Autowired
    private ProductService productService;

    @GetMapping("/odernow/{id}")
    @SuppressWarnings("unchecked")
    public String orderNow(@PathVariable(value = "id") Long id, HttpSession session) {

        if (session.getAttribute("cart") == null) {
            List<Item> cart = new ArrayList<>();
            Product product = this.productService.findById(id);
            cart.add(new Item(product, 1));
            session.setAttribute("cart", cart);
        } else {
            List<Item> cart = (List<Item>) session.getAttribute("cart");

            // using method isExisting here
            int index = isExisting(id, session);
            if (index == -1)
                cart.add(new Item(this.productService.findById(id), 1));
            else {
                int quantity = cart.get(index).getQuantity() + 1;
                cart.get(index).setQuantity(quantity);
            }

            session.setAttribute("cart", cart);
        }
        return "shopcart/cart";
    }

    @GetMapping("/delete/{id}")
    @SuppressWarnings("unchecked")
    public String delete(@PathVariable Long id, HttpSession session) {
        List<Item> cart = (List<Item>) session.getAttribute("cart");

        int index = isExisting(id, session);
        cart.remove(index);
        session.setAttribute("cart", cart);
        return "shopcart/cart";
    }

    @SuppressWarnings("unchecked")
    private int isExisting(Long id, HttpSession session) {
        List<Item> cart = (List<Item>) session.getAttribute("cart");

        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getProduct().getId() == id) {
                return i;
            }
        }
        return -1;
    }
}
