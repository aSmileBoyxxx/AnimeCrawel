package fan.zhunter.downloadanime.service.m4s;

import fan.zhunter.downloadanime.common.Binterface.IDriver;
import fan.zhunter.downloadanime.common.Binterface.IListParse;
import fan.zhunter.downloadanime.common.config.Env;
import fan.zhunter.downloadanime.util.JsonUtil;
import fan.zhunter.downloadanime.util.PatternUtil;
import fan.zhunter.downloadanime.util.ThreadLocalUtil;
import org.openqa.selenium.WebDriver;

import java.util.*;

/**
 * 1. 在Driver的Cookie已经设置的条件下获取list
 * 2. env中获取driver，根据xpath爬取
 * */
public class BiliBiliPlayList implements IListParse {
    final private String api = "https://api.bilibili.com/pgc/review/user?media_id=";
    final private String apiJp = "result.media.season_id";
    final private String listApi = "https://api.bilibili.com/pgc/web/season/section?season_id=";
    final private String regFir = "<div class=\"media-right\"><a href=\".*?\" target";
    final private String regSec = "<meta property=\"og:url\" content=\".*?\">";
    @Override
    public Map<String, String> getPlayList(Map<String, String> url,List<Integer> playList) {
        if(url.size() != 1){
            return new HashMap<>();
        }
        Env env = ThreadLocalUtil.getEnv();
        IDriver driver = env.getDriver();
        WebDriver webDriver = driver.getDriver();
        String name = new ArrayList<>(url.keySet()).get(0);
        String listUrl = url.get(name);
        webDriver.get(listUrl);
        String page = webDriver.getPageSource();
        String start = PatternUtil.pattern(page,regFir);
        if(start == null){
            start = PatternUtil.pattern(page, regSec);
        }
        start = start.substring(start.indexOf("md")+2, start.lastIndexOf("/"));
        String tmp = api + start;
        webDriver.get(tmp);
        page = webDriver.getPageSource();
        String json = page.substring(page.indexOf("{"), page.lastIndexOf("}") + 1);
        String sessid = JsonUtil.parse(json,apiJp);
        tmp = listApi + sessid;
        webDriver.get(tmp);
        page = webDriver.getPageSource();
        json = page.substring(page.indexOf("{"), page.lastIndexOf("}") + 1);
        Map<String, String> map = new HashMap<>();
        int listSize = JsonUtil.getListSize(json,"result.main_section.episodes");
//        for (int i = 0; i < listSize; i++) {
        for (Integer i : playList){
            map.put(JsonUtil.parse(json, "result.main_section.episodes[" + (i-1) + "].long_title"),
                    JsonUtil.parse(json, "result.main_section.episodes["+ (i-1) +"].share_url"));
        }
        return map;
    }

//    private final String css1 = "#eplist_module > div.list-wrapper > ul";
//    private final String simple = "//*[@id=\"app\"]/div[2]/div[2]/div/div[1]/div/div[1]/div/div/div[2]/ul/li[2]";
//    private final String css2 = "#app > div.media-tab-wrp > div.media-tab-content > div > div.media-tab-detail-l-wrp > div > " +
//            "div:nth-child(1) > div > div > div.sl-ep-list > ul";
//    @Override
//    public Map<String, String> getPlayList(Map<String, String> url, List<Integer> playList) {
//        if(url.size() != 1){
//            return new HashMap<>();
//        }
//        Env env = ThreadLocalUtil.getEnv();
//        IDriver driver = env.getDriver();
//        WebDriver webDriver = driver.getDriver();
//        String name = new ArrayList<>(url.keySet()).get(0);
//        String listUrl = url.get(name);
//        webDriver.get(listUrl);
//        WebElement ul = null;
//        Map<String, String> map = null;
//        try {
//            Thread.sleep(2000);
//            ul = webDriver.findElement(By.cssSelector(css1));
//            if(ul != null) map = driectGet(ul, webDriver, playList, name);
//        }catch (Exception e){}
//        if(ul == null){
//            try {
//                Thread.sleep(2000);
//                webDriver.findElement(By.xpath(simple)).click();
//                ul = webDriver.findElement(By.cssSelector(css2));
//                if(ul != null) map = skipAndGet(ul, webDriver, playList, name);
//            }catch (Exception e){}
//        }
//        return map;
//    }
//    public Map<String, String>  skipAndGet(WebElement ul, WebDriver webDriver, List<Integer> playList, String name){
//        HashMap<String, String> map = new HashMap<>();
//        try {
//            List<WebElement> lis = ul.findElements(By.cssSelector("li"));
//            Set<String> windowHandles = webDriver.getWindowHandles();
//            String rowHandler = new ArrayList<>(windowHandles).get(0);
//            for (Integer num : playList){
//                WebElement li = lis.get(num - 1);
//                String epName = name + "\\" + li.getAttribute("outerHTML").split("title=")[1].split(" ")[0];
//                li.click();
//                ArrayList<String> strings = new ArrayList<>(webDriver.getWindowHandles());
//                strings.remove(rowHandler);
//                String nowHandler = strings.get(0);
//                webDriver.switchTo().window(nowHandler);
//                String playUrl = webDriver.getCurrentUrl();
//                map.put(epName, playUrl);
//                webDriver.switchTo().window(nowHandler).close();
//                webDriver.switchTo().window(rowHandler);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return map;
//    }
//    public Map<String, String>  driectGet(WebElement ul, WebDriver webDriver, List<Integer> playList, String name){
//        HashMap<String, String> map = new HashMap<>();
//        try {
//            List<WebElement> lis = ul.findElements(By.cssSelector("li"));
//            Set<String> windowHandles = webDriver.getWindowHandles();
//            String rowHandler = new ArrayList<>(windowHandles).get(0);
//            while (lis.size() == 0){
//                System.out.println(lis.size());
//            }
//            for (Integer num : playList){
//                WebElement li = lis.get(num - 1);
//                String epName = name + "\\" + li.getAttribute("outerHTML").split("title=")[1].split(" ")[0];
//                li.click();
//                String playUrl = webDriver.getCurrentUrl();
//                map.put(epName, playUrl);
//                webDriver.switchTo().window(rowHandler);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return map;
//    }
}
