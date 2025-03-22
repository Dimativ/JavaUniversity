package org.lytvinenko.com.kastaparsinglytvinenko.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lytvinenko.com.kastaparsinglytvinenko.model.Product;
import org.lytvinenko.com.kastaparsinglytvinenko.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> fetchAndSaveProducts(String query) {
        List<Product> products = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://kasta.ua/ru/search/?q=" + encodedQuery;
            Document doc = Jsoup.connect(url).get();

            Elements productElements = doc.select("article.p__item");

            for (Element el : productElements) {
                Product product = new Product();

                product.setExternalId(el.attr("id"));

                Element nameEl = el.selectFirst(".p__info_name");
                if (nameEl != null) {
                    product.setName(nameEl.text());
                }

                Element priceEl = el.selectFirst(".product_item__new-cost");
                if (priceEl != null) {
                    product.setPrice(priceEl.text());
                }

                Element oldPriceEl = el.selectFirst(".product_item__old-cost");
                if (oldPriceEl != null) {
                    product.setOldPrice(oldPriceEl.text());
                }

                Element imgEl = el.selectFirst(".p__img img");
                if (imgEl != null) {
                    String imageUrl = imgEl.hasAttr("data-src") ? imgEl.attr("data-src") : imgEl.attr("src");
                    product.setImageUrl(imageUrl);
                }

                Element linkEl = el.selectFirst(".p__img a");
                if (linkEl != null) {
                    product.setProductUrl("https://kasta.ua" + linkEl.attr("href"));
                }

                products.add(product);
            }

            productRepository.saveAll(products);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }
}