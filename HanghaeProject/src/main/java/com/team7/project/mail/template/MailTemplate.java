package com.team7.project.mail.template;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class MailTemplate {

    String htmlBodyBeforeName="    <body style=\"margin: 0; padding: 0; width:600px; font-family:Arial ;\">\n" +
            "        <div style=\"margin: 20px\">\n" +
            "        <header>\n" +
            "            <a style=\"height: 30px;\" href =\"https://willbedeveloper.com\">\n" +
            "               <img src=\"cid:logo\" alt=\"logo\"/>\n" +
            "            </a></header>\n<section>\n" +
            "            <h1 style=\"font-weight: 700; font-size: 24px; line-height: 30px; margin:12px 0px;\">✨메일 인증 안내입니다✨</h1>\n" +
            "        <p style=\" margin: 2px ;color:#6D727C; font-weight: 400; font-size: 14px; line-height: 22px;\">\n" +
            "            안녕하세요. <span style=\"font-weight: 500; color:#50555E; \">";
    String htmlBotyAfterName = "님</span>!\n" +
            "            <br>화상면접 플렛폼 윌비의 회원이 되신걸 환영합니다!\n" +
            "            <br>\n" +
            "            <br>아래 메일 인증하기 버튼을 눌러 이메일 인증을 완료하셔야\n" +
            "            <br>정상적인 서비스 이용이 가능합니다.\n" +
            "            <br>앞으로 윌비 서비스에 많은 사랑 부탁드립니다.\uD83D\uDC97\n" +
            "            <br>\n" +
            "            <br>감사합니다.\n" +
            "            <br>  \n" +
            "        </p>\n" +
            "        <div style =\"align-items: center; text-align: center; margin: 32px 0px;\">\n" +
            "            <p style=\"color:#6D727C; font-weight: 400; font-size: 14px; line-height: 22px; margin-bottom: 32px;\">이메일 인증하러가기\uD83D\uDC47</p>\n" +
            "            <a style =\"background-color: #567FE8; \n" +
            "            width:220px ; text-decoration: none; color: #fff; padding: 12px 80px 12px 80px;\n" +
            "            border-radius: 8px; \n" +
            "            \" href =\"https://willbedeveloper.com/signin/validation?token=";
    String middletoken = "&email=";

    String endEmail = "\">메일 인증</a>\n" +
            "       </div>\n" +
            "            \n" +
            "        </section>\n" +
            "    </div>\n" +
            "        <footer style=\"background-color: #F4F6F9; padding: 20px;\">\n" +
            "        <div style=\"color:#6D727C; font-weight: 500; font-size:12px; line-height: 14px;\">\n" +
            "            <p>고객센터</p>\n" +
            "            <h1 style=\"color:#50555E; font-weight: 700; font-size:16px; line-height: 20px;\">Willbe.info.7@gmail.com</h1>\n" +
            "            <div>\n" +
            "                <p>AM 09:00 ~ PM 6:00 Off-time PM 13:00 ~ 14:00</p>\n" +
            "                <P>주말, 공휴일 휴무</P>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        <div style=\"color:#6D727C; font-weight: 400; font-size:12px; line-height: 14px; margin-top:24px\">\n" +
            "            <p><span>Will Be</span><span>항해 project : team 7</span> </p>\n" +
            "            <p>주소: 비밀~\uD83D\uDD12</p></div>\n" +
            "        </footer>\n" +
            "    </body>";


    public String getTemplate(String token, String email, String name){
        return htmlBodyBeforeName + name + htmlBotyAfterName + token +middletoken +email + endEmail;
    }

}
