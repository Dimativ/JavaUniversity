package org.lytvinenko.com.kastaparsinglytvinenko.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.lytvinenko.com.kastaparsinglytvinenko.model.ExchangeRate;
import org.lytvinenko.com.kastaparsinglytvinenko.model.Product;
import org.lytvinenko.com.kastaparsinglytvinenko.repository.ProductRepository;
import org.lytvinenko.com.kastaparsinglytvinenko.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Controller
public class WebController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    // Стартовая страница с формой ввода товара
    @GetMapping("/")
    public String index(Model model) throws JsonProcessingException {
        String url = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        List<ExchangeRate> rates = mapper.readValue(response, new TypeReference<List<ExchangeRate>>() {});
        model.addAttribute("exchangeRates", rates);
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        productRepository.deleteAll();
        List<Product> products = productService.fetchAndSaveProducts(query);
        model.addAttribute("products", products);
        return "results"; // results.html
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=products.xlsx";
        response.setHeader(headerKey, headerValue);

        List<Product> listProducts = productRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Результати пошуку");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("External ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Price");
            headerRow.createCell(3).setCellValue("Old Price");
            headerRow.createCell(4).setCellValue("Image URL");
            headerRow.createCell(5).setCellValue("Product URL");

            int rowCount = 1;
            for (Product product : listProducts) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(product.getExternalId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getPrice());
                row.createCell(3).setCellValue(product.getOldPrice());
                row.createCell(4).setCellValue(product.getImageUrl());
                row.createCell(5).setCellValue(product.getProductUrl());
            }

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.close();
        }
    }
}
