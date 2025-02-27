package ml.melun.mangaview;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static ml.melun.mangaview.MainApplication.httpClient;
import static ml.melun.mangaview.MainApplication.p;

public class UrlUpdater extends AsyncTask<Void, Void, Boolean> {
    String result;
    String fetchUrl;
    boolean silent = false;
    Context c;
    UrlUpdaterCallback callback;
    public UrlUpdater(Context c){
        this.c = c;
        this.fetchUrl = p.getDefUrl();
    }
    public UrlUpdater(Context c, boolean silent, UrlUpdaterCallback callback, String defUrl){
        this.c = c;
        this.silent = silent;
        this.callback = callback;
        this.fetchUrl = defUrl;
    }
    protected void onPreExecute() {
        if(!silent) Toast.makeText(c, "자동 URL 설정중...", Toast.LENGTH_SHORT).show();
    }
    protected Boolean doInBackground(Void... params) {
        return fetch();
    }

    protected Boolean fetch(){
        try {
            // URL에서 HTML을 가져옵니다.
            String url = "https://www.xn--h10b2b940bwzy.store/";
            Document document = Jsoup.connect(url).get();

            // 정확히 <a> 태그에서 data-testid="linkElement"와 aria-label="마나토끼 바로가기"를 만족하는 엘리먼트 선택
            Element linkElement = document.selectFirst("a[data-testid=\"linkElement\"][aria-label=\"마나토끼 바로가기\"]");

            // href 속성을 반환
            if (linkElement != null) {
                result = linkElement.attr("href");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Deprecated
    protected Boolean fetch_(){
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
            Response r = httpClient.get(fetchUrl, headers);
            if (r.code() == 302) {
                result = r.header("Location");
                r.close();
                return true;
            } else{
                r.close();
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    protected void onPostExecute(Boolean r) {
        if(r && result !=null){
            p.setUrl(result);
            if(!silent)Toast.makeText(c, "자동 URL 설정 완료!", Toast.LENGTH_SHORT).show();
            if(callback!=null) callback.callback(true);
        }else{
            if(!silent)Toast.makeText(c, "자동 URL 설정 실패, 잠시후 다시 시도해 주세요", Toast.LENGTH_LONG).show();
            if(callback!=null) callback.callback(false);
        }


    }


    public interface UrlUpdaterCallback{
        void callback(boolean success);
    }
}
