package pcshop.duancanhan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pcshop.duancanhan.pojo.Product;
import pcshop.duancanhan.service.BuildPcService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/build-pc")
public class BuildPcController {

    @Autowired
    private BuildPcService buildPcService;

    @GetMapping
    public String showBuildPc(Model model) {
        model.addAttribute("categories", buildPcService.getComponentCategories());
        model.addAttribute("selectedComponents", buildPcService.getSelectedComponents());
        model.addAttribute("pageTitle", "Lắp ráp PC");
        return "build-pc";
    }

    @GetMapping("/select/{productId}")
    public String selectComponent(@PathVariable Long productId, Model model) {
        List<Product> relatedProducts = buildPcService.getRelatedProducts(productId);
        Map<String, Product> selectedComponents = buildPcService.getSelectedComponents();
        if (relatedProducts != null && !relatedProducts.isEmpty()) {
            Product selectedProduct = relatedProducts.get(0);
            if (selectedProduct != null && selectedProduct.getCategory() != null) {
                selectedComponents.put(selectedProduct.getCategory().getName(), selectedProduct);
            } else {
                model.addAttribute("error", "Sản phẩm không hợp lệ hoặc thiếu danh mục.");
            }
        } else {
            model.addAttribute("error", "Sản phẩm với ID " + productId + " không tồn tại hoặc không có sản phẩm liên quan. Vui lòng kiểm tra lại hoặc liên hệ hỗ trợ.");
        }
        model.addAttribute("categories", buildPcService.getComponentCategories());
        model.addAttribute("selectedComponents", selectedComponents);
        model.addAttribute("relatedProducts", relatedProducts != null ? relatedProducts : List.of());
        model.addAttribute("pageTitle", "Lắp ráp PC");
        return "build-pc";
    }

    @GetMapping("/select-category/{categoryId}")
    public String selectCategory(@PathVariable Long categoryId, Model model) {
        List<Product> products = buildPcService.getProductsByCategoryId(categoryId);
        Map<String, Product> selectedComponents = buildPcService.getSelectedComponents();
        if (products != null && !products.isEmpty()) {
            model.addAttribute("relatedProducts", products);
        } else {
            model.addAttribute("error", "Không tìm thấy sản phẩm nào cho category ID " + categoryId + ".");
        }
        model.addAttribute("categories", buildPcService.getComponentCategories());
        model.addAttribute("selectedComponents", selectedComponents);
        model.addAttribute("pageTitle", "Lắp ráp PC - Danh mục " + categoryId);
        return "build-pc";
    }

    @PostMapping("/add-to-cart")
    public String addBuildToCart(Model model) {
        Map<String, Product> selectedComponents = buildPcService.getSelectedComponents();
        if (selectedComponents != null && !selectedComponents.isEmpty() && buildPcService.checkCompatibility(selectedComponents)) {
            try {
                buildPcService.addBuildToCart(selectedComponents);
                return "redirect:/cart";
            } catch (Exception e) {
                model.addAttribute("error", "Lỗi khi thêm vào giỏ hàng: " + e.getMessage());
            }
        } else if (selectedComponents == null || selectedComponents.isEmpty()) {
            model.addAttribute("error", "Vui lòng chọn ít nhất một linh kiện trước khi thêm vào giỏ hàng.");
        } else {
            model.addAttribute("error", "Các linh kiện không tương thích!");
        }
        model.addAttribute("categories", buildPcService.getComponentCategories());
        model.addAttribute("selectedComponents", selectedComponents != null ? selectedComponents : new HashMap<>());
        model.addAttribute("pageTitle", "Lắp ráp PC");
        return "build-pc";
    }
}