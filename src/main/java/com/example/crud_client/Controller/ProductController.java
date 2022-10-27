package com.example.crud_client.Controller;

import com.example.crud_client.Model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class ProductController {
    private final String REST_API_LIST = "http://localhost:8085/api/v1/product/getList";
    private final String REST_API_CREATE = "http://localhost:8085/api/v1/product/create";
    private final String REST_API_SELL = "http://localhost:8085/api/v1/product/sell?";

    private static Client createJerseyRestClient() {
        ClientConfig clientConfig = new ClientConfig();
        // Config logging for client side
        clientConfig.register( //
                new LoggingFeature( //
                        Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME), //
                        Level.INFO, //
                        LoggingFeature.Verbosity.PAYLOAD_ANY, //
                        10000));
        return ClientBuilder.newClient(clientConfig);
    }

    @GetMapping(value = "/getProducts")
    public String index(Model model) {
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_LIST);
        List<Product> products = target.request(MediaType.APPLICATION_JSON_TYPE).get(List.class);
        model.addAttribute("products", products);
        return "index";
    };

    @PostMapping(value = "createProduct")
    public String create(@RequestParam String name,
                         @RequestParam int price,
                         @RequestParam int quantity){
        Product product = new Product();
        product.setName(name);
        product.setQuantity(quantity);
        product.setPrice(price);
        String jsonUser = convertToJson(product);
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_CREATE);
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(jsonUser, MediaType.APPLICATION_JSON));
        return "redirect:/getProducts";
    }

    @PostMapping(value = "/sellProduct")
    public String sell(@RequestParam int id, @RequestParam int quantity){
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_SELL+"id= "+id+"&quantity= "+quantity);
        return "redirect:/getProducts";
    }
    @GetMapping(value = "formCreate")
    public String getViewCreate(){
        return "create";
    }

    @GetMapping(value = "formSell")
    public String getViewSell(Model model){
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_LIST);
        List<Product> products = target.request(MediaType.APPLICATION_JSON_TYPE).get(List.class);
        model.addAttribute("products", products);
        return "sell";
    }

    private static String convertToJson(Product product) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(product);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
