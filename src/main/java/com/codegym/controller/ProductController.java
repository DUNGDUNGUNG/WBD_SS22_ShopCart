package com.codegym.controller;

import com.codegym.model.Product;
import com.codegym.model.ProductForm;
import com.codegym.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;

@Controller
@PropertySource("classpath:global_config_app.properties")
public class ProductController {
    @Autowired
    Environment env;

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public ModelAndView listProduct(){
        Iterable<Product>products=productService.findAll();
        return new ModelAndView("product/list","products",products);
    }

    @GetMapping("/create-product")
    public ModelAndView showCreateForm(){
        return new ModelAndView("product/create","productForm",new ProductForm());
    }

    @PostMapping("/save-product")
    public ModelAndView saveProduct(@ModelAttribute ProductForm productForm, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            System.out.println("Result error occur" +bindingResult.getAllErrors());
        }

        MultipartFile multipartFile= productForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        String fileUpload = env.getProperty("file_upload").toString();

        try {
            FileCopyUtils.copy(productForm.getImage().getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Product productObject = new Product(productForm.getName(),fileName,productForm.getPrice());
        productService.save(productObject);

        ModelAndView modelAndView = new ModelAndView("product/create","productForm", new ProductForm());
        modelAndView.addObject("message","New product created successfully");
        return modelAndView;
    }

    @GetMapping("/view-product/{id}")
    public ModelAndView viewProduct(@PathVariable Long id){
        Product product = productService.findById(id);
        ModelAndView  modelAndView = new ModelAndView("product/view","product",product);
        return modelAndView;
    }
}
