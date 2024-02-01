package com.blog.som.global.util;

import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HtmlParser {

  private HtmlParser() {}

  public static List<String> getImageList(String html) {
    Document document = Jsoup.parse(html);
    Elements elements = document.select("img");
    return elements.stream().map(e -> e.attr("src"))
        .toList();
  }

}