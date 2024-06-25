package jpabook.jpashop.controller.ui.product;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductUIController {

    @GetMapping("/list")
    public String getProductList(Model model){
        return "/product/list";
    }

}
