package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.enumm.Role;
import com.example.springsecurityapplication.enumm.Status;
import com.example.springsecurityapplication.models.*;
import com.example.springsecurityapplication.repositories.CategoryRepository;
import com.example.springsecurityapplication.repositories.OrderRepository;

import com.example.springsecurityapplication.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class AdminController {

    private final ProductService productService;

    private final PersonService personService;

    private final OrderService orderService;

    private final OrderRepository orderRepository;


    @Value("${upload.path}")
    private String uploadPath;

    private final CategoryRepository categoryRepository;

    public AdminController(ProductService productService, PersonService personService, OrderService orderService, OrderRepository orderRepository, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.personService = personService;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
    }

    //Главная страница личного кабинета администратора
    @GetMapping("/admin")
    public String productGetAll(Model model) {
        model.addAttribute("products", productService.getAllProduct());
        return "admin";
    }

    //Добавление продукта
    @GetMapping("admin/product/add")
    public String productAdd(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/addProduct";
    }

    //Добавление продукта
    @PostMapping("/admin/product/add")
    public String productAdd(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @RequestParam("file_one") MultipartFile file_one, @RequestParam("file_two") MultipartFile file_two, @RequestParam("file_three") MultipartFile file_three, @RequestParam("file_four") MultipartFile file_four, @RequestParam("file_five") MultipartFile file_five, @RequestParam("category") int category, Model model) throws IOException {
        Category category_db = (Category) categoryRepository.findById(category).orElseThrow();
        System.out.println(category_db.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("category", categoryRepository.findAll());
            return "product/addProduct";
        }

        if (file_one != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_one.getOriginalFilename();
            file_one.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        if (file_two != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_two.getOriginalFilename();
            file_two.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        if (file_three != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_three.getOriginalFilename();
            file_three.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        if (file_four != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_four.getOriginalFilename();
            file_four.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }

        if (file_five != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_five.getOriginalFilename();
            file_five.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }
        productService.saveProduct(product, category_db);
        return "redirect:/admin";
    }


    //Удаление продукта
    @GetMapping("admin/product/delete/{id}")
    public String productDelete(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    //Редактирование продукта
    @GetMapping("admin/product/edit/{id}")
    public String productEdit(Model model, @PathVariable("id") int id) {
        model.addAttribute("product", productService.getProductId(id));
        model.addAttribute("category", categoryRepository.findAll());
        return "product/editProduct";
    }

    //Редактирование продукта
    @PostMapping("admin/product/edit/{id}")
    public String productEdit(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult, @PathVariable("id") int id, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("category", categoryRepository.findAll());
            return "product/editProduct";
        }
        productService.updateProduct(id, product);
        return "redirect:/admin";
    }

    //Список всех заказов
    @GetMapping("/admin/orderList")
    public String ordersGetAll(Model model) {
        model.addAttribute("orders", orderService.getAllOrders1());
        model.addAttribute("status", Status.values());
        return "/admin/orderList";
    }

    //Поиск заказа по последним 4м символам номера
    @PostMapping("/admin/orderList/order_search")
    public String orderSearch(@RequestParam("order_search") String search, Model model) {
        model.addAttribute("orders", orderService.getAllOrders1());
        model.addAttribute("order_search", orderRepository.findByNumber(search));
        model.addAttribute("value_search", search);
        return "admin/orderList";
    }

    //Информация о выбранном заказе
    @GetMapping("/admin/order/info/{id}")
    public String orderInfo(@PathVariable("id") int id, Model model) {
        model.addAttribute("orders", orderService.getOrderById(id));
        model.addAttribute("status", Status.values());
        return "/admin/orderInfo";
    }

    //Изменить статус заказа
    @PostMapping("/admin/order/info/{id}")
    public String orderStatusChange(@ModelAttribute("status") Status status, @PathVariable("id") int id) {
        Order order = orderService.getOrderById(id);
        order.setStatus(status);
        orderService.updateOrder(id, order);
        return "redirect:/admin/orderList";

    }

    //Список всех пользователей
    @GetMapping("/user/list")
    public String personGetAll(Model model) {
        model.addAttribute("persons", personService.getAllPerson());
        return "/user/list";
    }

    //Информация о выбранном пользователе
    @GetMapping("/user/info/{id}")
    public String personInfo(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", personService.getPersonId(id));
        model.addAttribute("role", Role.values());
        return "/user/infoUser";
    }

    //Сменить роль пользователя
    @PostMapping("/user/info/{id}")
    public String personRoleChange (@ModelAttribute("role") String role, @PathVariable("id") int id){
        Person person = personService.getPersonId(id);
        person.setRole(role);
        personService.updatePerson(id, person);
        return "redirect:/user/list";
    }
}
