package com.weixinpay;

public class Constants {
	// APP_ID �滻Ϊ���Ӧ�ôӹٷ���վ���뵽�ĺϷ�appId
 //   public static final String APP_ID = "wx0c58d511ff005b6f";
    
    /***----------以下是菏泽的建行参数信息---------***/
    public static final String APPURL = "https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain";
    /**
     * 商户代码，已加密
     */
    public static final String MERCHANTID = "acomccmmccmmccmmccmrcctqccrmccnq";
    /**
     * 商户柜台代码，已加密
     */
    public static final String POSID = "dfikffjiffngffgkffkl";
    /**
     * 分行代码，已加密
     */
    public static final String BRANCHID = "dewweewweewweewdfezd";
    /**
     * 公钥(手机)
     */
    public static final String PubKey = "30819d300d06092a864886f70d010101050003818b0030818702818100a63f2754c17f228f8a3849f36f58"
            + "0e733cb7573aad3008667a28d4bc258e9dc2a398d98fb4b19023946fd22dac278e43c24ea6fa116a9725c34e6a2a9ddcdd8f1b4c7dd13ffb"
            + "d95b0cefcbb6f080c3d53fe4eb259b0a184a03d732aedb6786610ecd48718d55c254a3742b081d1d58eee2da78089bfbb09d6637d765c209"
            + "95d5020113";
//    public static final String PubKey = "30819c300d06092a864886f70d010101050003818a003081860281805ad47ab97e043d7c9197591effb0b5b9232bc7cab00ff78c40ff112cdfa63586d63eab9c381078e16431fb391345a762f5411dc051015f526d61753c5aaa946d3047774b2a86bbb06f79f9b24c365590af163206153932d1c43b76b4baf62fd4999a0ae76b9b25c51249ae9d6b6ddc52b8a56b2cbc7a376ca791af26a67a24c3020113";
    /***----------------------------------------***/

    public static class ShowMsgActivity {
		public static final String STitle = "showmsg_title";
		public static final String SMessage = "showmsg_message";
		public static final String BAThumbData = "showmsg_thumb_data";
	}
}
