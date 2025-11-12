package com.fsb.BatSystem;

import com.fsb.entity.*;
import com.fsb.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class SystemWindow {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private RacketSupplierService racketSupplierService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private RacketService racketService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;


    Scanner scanner = new Scanner(System.in);
    public void show() {
        while (true) {
            System.out.println("\n==== 球拍信息管理系统 ====");
            System.out.println("1. 登录\n2. 注册\n3. 退出");
            String option = scanner.nextLine();
            switch (option) {
                case "1" -> login();
                case "2" -> register();
                case "3" -> System.exit(0);
                default -> System.out.println("无效选项");
            }
        }
    }
    //登录
    public void login() {
        while (true) {
            System.out.println("请输入用户名：");
            String username = scanner.nextLine();
            System.out.println("请输入密码：");
            String password = scanner.nextLine();
            Customer customer = customerService.findByUsernameAndPassword(username, password);
            if (username.equals("admin") && password.equals("admin")) {
                adminMenu();
                return;
            }else if (customer!=null && customer.getCustomerID() > 0) {
                buyerMenu(customer);
                return;
            } else
                System.out.println("用户名或密码错误,请重试");
        }
    }
    //注册
    public void register() {
        while (true) {
            System.out.println("请输入用户名:");
            String username = scanner.nextLine();
            System.out.println("请输入密码:");
            String password = scanner.nextLine();
            if (customerService.findByUsername(username).size()>0) {
                System.out.println("用户名已存在,请重新输入");
            } else {
                Customer customer1 = new Customer(username, password);
                customerService.add(customer1);
                System.out.println("注册成功");
                return;
            }
        }
    }
    //买家菜单
    public void buyerMenu(Customer customer) {
        while (true) {
            System.out.println("\n==== 买家菜单 ====");
            System.out.println("1. 下单购买");
            System.out.println("2. 查看本人订单明细");
            System.out.println("3. 查看球拍属性");
            System.out.println("4. 查看球拍供应商");
            System.out.println("5. 查看球拍品牌");
            System.out.println("6. 查看球拍库存");
            System.out.println("7. 查看商品评价");
            System.out.println("8. 发布评价");
            System.out.println("9. 修改评价");
            System.out.println("10. 返回");
            String option = scanner.nextLine();
            switch (option) {
                case "1":
                    System.out.println("请输入要购买的球拍编号：");
                    int racketID = Integer.parseInt(scanner.nextLine());
                    System.out.println("请输入购买数量：");
                    int quantity = Integer.parseInt(scanner.nextLine());
                    if (orderService.placeOrder(customer, racketID, quantity)) {
                        System.out.println("下单成功！");
                    } else {
                        System.out.println("下单失败，请检查商品或库存信息");
                    }
                    break;
                case "2":
                    System.out.println("您的订单明细如下:");
                    for (Order order : orderService.findById(customer.getCustomerID())) {
                        System.out.println(order);
                    }
                    break;
                case "3":
                    racketService.racketList().forEach(System.out::println);
                    break;
                case "4":
                    supplierService.findAll().forEach(System.out::println);
                    break;
                case "5":
                    brandService.findAll().forEach(System.out::println);
                    break;
                case "6":
                    inventoryService.findAll().forEach(System.out::println);
                    break;
                case "7":
                    reviewService.findAll().forEach(System.out::println);
                    break;
                case "8":
                    System.out.println("请输入您要评价的球拍编号:");
                    int racketID8 =Integer.parseInt(scanner.nextLine());
                    System.out.println("请输入您的评价星级(1-5):");
                    int rating = Integer.parseInt(scanner.nextLine());
                    System.out.println("请输入您的评价:");
                    String comment = scanner.nextLine();
                    Review review = new Review();
                    review.setRacketID(racketID8);
                    review.setRating(rating);
                    review.setCustomerID(customer.getCustomerID());
                    review.setComment(comment);
                    if(reviewService.add(review)){
                        System.out.println("评价成功");
                    }
                    else
                        System.out.println("评价失败");
                    break;
                case "9":
                    System.out.println("请输入您要修改的评价的编号:");
                    int reviewID = Integer.parseInt(scanner.nextLine());

                    Review review9 = reviewService.findById(reviewID);
                    if(reviewService.exists(review9)){
                        System.out.println("当前评论不存在，无法修改！！！");
//                        return;
                        break;
                    }

                    //检查评论是否属于当前用户
                    if (review9.getCustomerID() != customer.getCustomerID()) {
                        System.out.println("❌ 无权修改他人评价！");
                        break;
                    }
                    System.out.println("请输入您的评价星级(1-5):");
                    rating = Integer.parseInt(scanner.nextLine());
                    System.out.println("请输入您的评价:");
                    comment = scanner.nextLine();
                    review = reviewService.findById(reviewID);
                    review.setRating(rating);
                    review.setComment(comment);
                    review.setReviewDate(new java.util.Date());
                    if(reviewService.update(review)){
                        System.out.println("修改成功");
                    }
                    else
                        System.out.println("修改失败");
                    break;
                case "10":
                    return;
                default:
                    System.out.println("无效选项");
                    break;
            }
        }
    }
   //管理员菜单（卖家菜单）
    public void adminMenu() {
        while (true) {
            System.out.println("=== 球拍信息管理系统（管理员端） ===");
            System.out.println("1. 球拍管理");
            System.out.println("2. 品牌管理");
            System.out.println("3. 供应商管理");
            System.out.println("4. 库存管理");
            System.out.println("5. 订单管理");
            System.out.println("6. 评价管理");
            System.out.println("7. 用户管理");
            System.out.println("8. 退出");
            System.out.print("请选择模块编号：");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    racketManagement();
                    break;
                case 2:
                    brandManagement();
                    break;
                case 3:
                    supplierManagement();
                    break;
                case 4:
                    inventoryManagement();
                    break;
                case 5:
                    orderManagement();
                    break;
                case 6:
                    reviewManagement();
                    break;
                case 7:
                    userManagement();
                    break;
                case 8:
                    System.out.println("退出系统。再见！");
                    return;
                default:
                    System.out.println("无效的选项，请重新输入。");
            }
        }
    }

    //用户管理
    private void userManagement() {
        while (true) {
            System.out.println("\n👤【客户管理】");
            System.out.println("1. 添加客户");
            System.out.println("2. 删除客户");
            System.out.println("3. 修改客户信息");
            System.out.println("4. 查看全部客户");
            System.out.println("5. 搜索客户");
            System.out.println("6. 返回上级");
            System.out.print("请选择操作：");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.print("请输入客户姓名:");
                    String name = scanner.nextLine();
                    System.out.print("请输入客户电话:");
                    String phone = scanner.nextLine();
                    System.out.print("请输入客户地址:");
                    String address = scanner.nextLine();
                    System.out.print("请输入客户注册时间:");
                    Date registerDate = Date.valueOf(scanner.nextLine());
                    System.out.print("请输入客户密码:");
                    String password = scanner.nextLine();
                    Customer customer = new Customer(null,  name, phone, address, registerDate, password,0);
                    if(customerService.add(customer))
                        System.out.println("添加成功！");
                    else
                        System.out.println("添加失败！");
                    break;
                case "2":
                    System.out.println("请输入要删除的客户编号:");
                    int id2 = Integer.parseInt(scanner.nextLine());
                    if(customerService.delete(id2))
                        System.out.println("删除成功！");
                    else
                        System.out.println("删除失败！");
                    break;
                case "3":
                    System.out.println("请输入要修改的客户编号:");
                    int id3 = Integer.parseInt(scanner.nextLine());
                    //先查询原有客户信息,有就改，没有就报错
                    Customer customer3 = customerService.findById(id3);
                    if(customer3==null)
                        System.out.println("该客户不存在！");
                    else {
                        System.out.print("请输入新的客户姓名:");
                        String name3 = scanner.nextLine();
                        System.out.print("请输入新的客户电话:");
                        String phone3 = scanner.nextLine();
                        System.out.print("请输入新的客户地址:");
                        String address3 = scanner.nextLine();
                        customer3.setName(name3);
                        customer3.setPhone(phone3);
                        customer3.setAddress(address3);
                        if (customerService.update(customer3))
                            System.out.println("修改成功");
                        else
                            System.out.println("修改失败");
                    }
                    break;
                case "4":
                    System.out.println("全部客户信息如下:");
                    for (Customer customer4 : customerService.findAll()) {
                        System.out.println(customer4);
                    }
                    break;
                case "5":
                    System.out.println("请输入要查询的客户名字");
                    String username = scanner.nextLine();
                    List<Customer> customer5 =customerService.findByUsername(username);
                    if(customer5.size()!=0) {
                        System.out.println("查询到下列信息:");
                        customer5.forEach(System.out::println);
                    }
                    else
                        System.out.println("当前条件下未找到");
                    break;
                case "6": return;
                default: System.out.println("❌ 无效选项");
            }
        }
    }

    //评价管理
    private void reviewManagement() {
        while (true) {
            System.out.println("\n【评价管理】");
            System.out.println("1. 查看全部评价");
            System.out.println("2. 删除评价");
            System.out.println("3. 返回上级");
            System.out.print("选择操作：");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("所有评价如下:");
                    reviewService.findAll().forEach(System.out::println);
                    break;
                case "2":
                    System.out.println("请输入要删除评价的编号:");
                    int id = Integer.parseInt(scanner.nextLine());
                    if(reviewService.delete(id))
                        System.out.println("删除成功！");
                    else
                        System.out.println("删除失败！");
                    break;
                case "3": return;
                default: System.out.println("❌ 无效输入");
            }
        }
    }

    //订单管理
    private void orderManagement() {
        while (true) {
            System.out.println("\n【订单管理】");
            System.out.println("1. 查看所有订单");
            System.out.println("2. 查看订单明细");
            System.out.println("3. 删除订单");
            System.out.println("4. 返回上级菜单");
            System.out.print("请选择操作:");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("所有订单信息如下:");
                    List<Map<String, Object>> orderServiceAll = orderService.findAll();
                    for (Map<String, Object> stringObjectMap : orderServiceAll) {
                        System.out.println("订单编号: "+stringObjectMap.get("OrderID")+
                                            ",客户编号: "+stringObjectMap.get("CustomerID")+
                                            ",客户姓名: "+stringObjectMap.get("Name")+
                                            ",总金额: "+stringObjectMap.get("Total")+
                                            ",下单时间: "+stringObjectMap.get("OrderDate"));
                    }
                    break;
                case "2":
                    System.out.println("请输入订单号");
                    int orderId = Integer.parseInt(scanner.nextLine());
                    OrderItem byId = orderItemService.findById(orderId);
                    if(byId!=null) {
                        System.out.println(byId);
                    }else
                        System.out.println("没有此订单");
                    break;
                case "3":
                    System.out.println("请输入订单号");
                    int orderId3 = Integer.parseInt(scanner.nextLine());
                    orderService.delete(orderId3);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("❌ 无效输入！");
            }
        }
    }

    //库存管理
    private void inventoryManagement() {
        while (true) {
            System.out.println("\n📦【库存管理】");
            System.out.println("1. 添加库存");
            System.out.println("2. 修改库存");
            System.out.println("3. 删除库存");
            System.out.println("4. 查询所有库存");
            System.out.println("5. 返回上级菜单");
            System.out.print("请选择操作:");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("请输入球拍编号:");
                    int racketID = Integer.parseInt(scanner.nextLine());
                    System.out.print("请输入球拍剩余量:");
                    int stock = Integer.parseInt(scanner.nextLine());
                    System.out.print("请输入存放位置:");
                    String warehouseLocation = scanner.nextLine();
                    Inventory inventory = new Inventory(null,racketID, stock, warehouseLocation,0);
                    if(inventoryService.add(inventory)){
                        System.out.println("添加成功！");
                    } else
                        System.out.println("添加失败！");
                    break;
                case "2":
                    System.out.print("请输入新的球拍编号:");
                    int racketID2 = Integer.parseInt(scanner.nextLine());
                    System.out.print("请输入新的球拍剩余量:");
                    int stock2 = Integer.parseInt(scanner.nextLine());
                    System.out.print("请输入新的存放位置:");
                    String warehouseLocation2 = scanner.nextLine();
                    Inventory inventory2 = new Inventory(null,racketID2, stock2, warehouseLocation2,0);
                    if(inventoryService.update(inventory2)){
                        System.out.println("修改成功！");
                    } else
                        System.out.println("修改失败！");
                    break;
                case "3":
                    System.out.println("请输入您要删除库存的球拍编号");
                    int racketID3 = Integer.parseInt(scanner.nextLine());
                    if(inventoryService.delete(racketID3)){
                        System.out.println("删除成功！");
                    }else
                        System.out.println("删除失败！");
                    break;
                case "4":
                    System.out.println("所有库存信息如下:");
                    for (Inventory inventory4 : inventoryService.findAll()) {
                        System.out.println(inventory4);
                    }
                    break;
                case "5":
                    return;
                default:
                    System.out.println("❌ 无效选项！");
            }
        }
    }

    //供应商管理
    private void supplierManagement() {
        while (true) {
            System.out.println("\n🏭【供应商管理】");
            System.out.println("1. 添加供应商");
            System.out.println("2. 修改供应商");
            System.out.println("3. 删除供应商");
            System.out.println("4. 查询所有供应商");
            System.out.println("5. 返回上级菜单");
            System.out.print("请选择操作:");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("请输入供应商名称:");
                    String name = scanner.nextLine();
                    System.out.print("请输入供应商电话:");
                    String phone = scanner.nextLine();
                    System.out.print("请输入供应类别:");
                    String supplyCategory = scanner.nextLine();
                    Supplier supplier = new Supplier(null, name, phone, supplyCategory,0);
                    if(supplierService.add(supplier)){
                        System.out.println("添加成功，新供应商编号为：" + supplier.getSupplierID());
                    }else
                        System.out.println("添加失败！");
                    break;
                case "2":
                    System.out.print("请输入新的供应商编号:");
                    int supplierID2 = Integer.parseInt(scanner.nextLine());
                    System.out.print("请输入新的供应商名称:");
                    String name2 = scanner.nextLine();
                    System.out.print("请输入新的供应商电话:");
                    String phone2 = scanner.nextLine();
                    System.out.print("请输入新的供应类别:");
                    String supplyCategory2 = scanner.nextLine();
                    Supplier supplier2 = new Supplier(supplierID2, name2, phone2, supplyCategory2,0);
                    if(supplierService.update(supplier2)){
                    System.out.println("修改成功！");
                    } else
                        System.out.println("修改失败！");
                    break;
                case "3":
                    System.out.print("请输入您要删除的供应商编号:");
                    int supplierID3 = Integer.parseInt(scanner.nextLine());
                    if(supplierService.delete(supplierID3)){
                        System.out.println("删除成功！");
                    }else
                        System.out.println("删除失败！");
                    break;
                case "4":
                    System.out.println("所有供应商信息如下:");
                    for (Supplier supplier1 : supplierService.findAll()) {
                        System.out.println(supplier1);
                    }
                    break;
                case "5":
                    return;
                default:
                    System.out.println("❌ 无效选项，请重新输入！");
            }
        }
    }

    //品牌管理
    private void brandManagement() {
        while (true) {
            System.out.println("\n📦【品牌管理】");
            System.out.println("1. 添加新的品牌");
            System.out.println("2. 修改当前品牌");
            System.out.println("3. 删除品牌");
            System.out.println("4. 查询所有品牌");
            System.out.println("5. 返回上级菜单");
            System.out.print("请选择操作:");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("请输入品牌名称:");
                    String brandName = scanner.nextLine();
                    System.out.print("请输入品牌产地:");
                    String brandCountry = scanner.nextLine();
                    System.out.print("请输入品牌网址:");
                    String brandWebsite = scanner.nextLine();
                    Brand brand = new Brand(null, brandName, brandCountry, brandWebsite,0);
                    if(brandService.add(brand)){
                        System.out.println("添加成功");
                    }else
                        System.out.println("添加失败");
                    break;
                case "2":
                    System.out.print("请输入更新后的品牌编号:");
                    int brandID2 = Integer.parseInt(scanner.nextLine());
                    System.out.print("请输入更新后的品牌名称:");
                    String brandName2 = scanner.nextLine();
                    System.out.print("请输入更新后的品牌产地:");
                    String brandCountry2 = scanner.nextLine();
                    System.out.print("请输入更新后的品牌网址:");
                    String brandWebsite2 = scanner.nextLine();
                    Brand brand2 = new Brand(brandID2, brandName2, brandCountry2, brandWebsite2,0);
                    if(brandService.update(brand2)){
                        System.out.println("修改成功");
                    }else
                        System.out.println("修改失败");
                    break;
                case "3":
                    System.out.print("请输入您要删除的品牌编号:");
                    int brandID3 = Integer.parseInt(scanner.nextLine());
                    if(brandService.delete(brandID3))
                        System.out.println("删除成功");
                    else
                        System.out.println("删除失败");
                    break;
                case "4":
                    System.out.println("所有品牌信息如下");
                    for (Brand brand1 : brandService.findAll()) {
                        System.out.println(brand1);
                    }
                    break;
                case "5":
                    return;
                default:
                    System.out.println("❌ 输入无效，请重试！");
            }
        }

    }


    //球拍管理哦
    private void racketManagement() {
        while (true) {
            System.out.println("\n=== 球拍管理 ===");
            System.out.println("1. 查看所有球拍");
            System.out.println("2. 添加新球拍");
            System.out.println("3. 修改当前球拍");
            System.out.println("4. 删除球拍");
            System.out.println("5. 返回上级菜单");
            System.out.print("请选择操作:");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.println("球拍信息如下:");
                    List<Racket> rackets = racketService.racketList();
                    for (Racket racket : rackets) {
                        System.out.println(racket);
                    }
                    break;
                case 2:
                    int racketID = getValidRacketId(true); // true 表示是新增操作
                    System.out.print("请输入品牌编号:");
                    int brandID = Integer.parseInt(scanner.nextLine());

                    if (!brandService.exists(brandID)) {
                        System.out.println("品牌编号不存在，请先添加该品牌或输入正确编号。");
                        break;
                    }

                    System.out.print("请输入球拍型号:");
                    String model = scanner.nextLine();
                    System.out.print("请输入球拍种类:");
                    String type = scanner.nextLine();
                    System.out.print("请输入球拍材质:");
                    String material = scanner.nextLine();
                    System.out.print("请输入球拍价格:");
                    double price = Double.parseDouble(scanner.nextLine());

                    Racket racket = new Racket(racketID, brandID, model, type, material, price, 0);
                    if (racketService.add(racket)) {
                        System.out.println("添加成功");
                    } else {
                        System.out.println("添加失败");
                    }
                    break;
                case 3:
                    int racketID3 = getValidRacketId(false); // false 表示是修改操作
                    System.out.print("请输入新的品牌编号:");
                    int brandID3 = Integer.parseInt(scanner.nextLine());

                    if (!brandService.exists(brandID3)) {
                        System.out.println("品牌编号不存在，请先添加该品牌或输入正确编号。");
                        break;
                    }

                    System.out.print("请输入新的球拍型号:");
                    String model3 = scanner.nextLine();
                    System.out.print("请输入新的球拍种类:");
                    String type3 = scanner.nextLine();
                    System.out.print("请输入新的球拍材质:");
                    String material3 = scanner.nextLine();
                    System.out.print("请输入新的球拍价格:");
                    double price3 = Double.parseDouble(scanner.nextLine());
                    Racket racket3 = new Racket(racketID3, brandID3, model3, type3, material3, price3,0);
                    if(racketService.update(racket3)) {
                        System.out.println("修改成功");
                    }else
                        System.out.println("修改失败");
                    break;
                case 4:
                    System.out.println("请输入您要删除的球拍编号:");
                    int racketID4 = Integer.parseInt(scanner.nextLine());
                    if(racketService.delete(racketID4))
                        System.out.println("删除成功");
                    else
                        System.out.println("删除失败");
                    break;
                case 5:
                    return;
                default:
                    System.out.println("无效的选项。");
            }
        }
    }

    // 新增一个工具方法用于获取有效的球拍编号
    private int getValidRacketId(boolean forAdd) {
        while (true) {
            System.out.print("请输入球拍编号:");
            int racketID = Integer.parseInt(scanner.nextLine());

            if (forAdd && racketService.exists(racketID)) {
                System.out.println("当前球拍编号已存在，请重新输入！");
            } else if (!forAdd && !racketService.exists(racketID)) {
                System.out.println("当前球拍编号不存在，无法进行修改，请重新输入！");
            } else {
                return racketID;
            }
        }
    }

}
