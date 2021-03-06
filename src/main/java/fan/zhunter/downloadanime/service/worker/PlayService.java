package fan.zhunter.downloadanime.service.worker;

import fan.zhunter.downloadanime.common.APlayService;
import fan.zhunter.downloadanime.common.Binterface.IDownUrlParse;
import fan.zhunter.downloadanime.common.requets.Request;

import java.util.Map;

public class PlayService extends APlayService {

    public PlayService(APlayService successor) {
        super(successor);
    }
    IDownUrlParse downParser;
    public void setDownParser(IDownUrlParse downParser) {
        this.downParser = downParser;
    }

    @Override
    public void handler(Request request) {
        Map<String, String> urls = request.getUrls();
        if(!request.isList()){
            if(downParser == null){
                System.err.println("IDownUrlParse is NULL! Please setting it");
            }else {
                downloadUrls = downParser.getDownLoadUrl(urls);
            }

        }
        if(successor != null){
            successor.handler(request);
        }
    }
}
