package com.vrv.sdk.library.action;

import android.text.TextUtils;

import com.vrv.imsdk.SDKClient;
import com.vrv.imsdk.api.ChatMsgApi;
import com.vrv.imsdk.bean.ContactBean;
import com.vrv.imsdk.bean.HiddenAccountInfoBean;
import com.vrv.imsdk.bean.LocalSettingBean;
import com.vrv.imsdk.bean.LoginExtraInfo;
import com.vrv.imsdk.bean.NoteInfoBean;
import com.vrv.imsdk.bean.PersonalDataBean;
import com.vrv.imsdk.model.Account;
import com.vrv.imsdk.model.ChatMsg;
import com.vrv.imsdk.model.ChatMsgBuilder;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.Group;
import com.vrv.imsdk.model.GroupMember;
import com.vrv.imsdk.model.SystemMsg;

import java.util.ArrayList;

/**
 * 请求同一管理入口
 * Created by Yang on 2015/10/29 029.
 */
public class RequestHelper {

    public static Account getMainAccount() {
        return SDKClient.instance().getAccountService().getCurrent();
    }

    public static long getUserID() {
        return SDKClient.instance().getAccountService().getUserID();
    }

    public static Contact getMyInfo() {
        return getMainAccount().getMyInfo();
    }

    public static boolean isMyself(long targetID) {
        return SDKClient.instance().getAccountService().isMyself(targetID);
    }

    /**
     * 预登录服务器返回状态
     *
     * @param serverName
     * @return
     */
    public static LoginExtraInfo getPreLoginInfo(String serverName) {
        return SDKClient.instance().getAuth().getPreLoginInfo(serverName);
    }

    /**
     * 上次登录信息自动登陆
     *
     * @param handler
     * @return
     */
    public static boolean autoLogin(RequestHandler handler) {
        return SDKClient.instance().getAuth().autoLogin(CallBackHelper.buildCallBack(handler));
    }

    /**
     * 手机登录
     *
     * @param user         用户名
     * @param pwd          密码
     * @param entArea      服务器地址
     * @param nationalCode 国家代码
     * @param handler
     * @return
     */
    public static boolean login(String user, String pwd, String entArea, String nationalCode, RequestHandler handler) {
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(entArea)) {
            return false;
        }
        return SDKClient.instance().getAuth().login(user, pwd, entArea, nationalCode, CallBackHelper.buildCallBack(handler));
    }

    public static boolean loginVerify(boolean next, String userName, String code, RequestHandler handler) {
        return SDKClient.instance().getAuth().loginVerify(next, userName, code, CallBackHelper.buildCallBack(handler));
    }

    // 退出
    public static boolean logout(RequestHandler handler) {
        return SDKClient.instance().getAuth().logout(CallBackHelper.buildCallBack(handler));
    }

    /**
     * 注册或忘记密码找回
     */
    public static boolean register(boolean register, String user, String entArea, String nationalCode, RequestHandler handler) {
        if (register) {
            return SDKClient.instance().getAuth().register(user, entArea, nationalCode, CallBackHelper.buildCallBack(handler));
        } else {
            return SDKClient.instance().getAuth().forgetPassword(user, entArea, nationalCode, CallBackHelper.buildCallBack(handler));
        }
    }

    // 验证校验码
    public static boolean verifyCode(boolean register, long registerID, String authCode, RequestHandler handler) {
        if (register) {
            return SDKClient.instance().getAuth().registerVerify(registerID, authCode, CallBackHelper.buildCallBack(handler));
        } else {
            return SDKClient.instance().getAuth().forgetPasswordVerify(registerID, authCode, CallBackHelper.buildCallBack(handler));
        }
    }

    // 注册/找回密码第2步
    public static boolean registerStep(boolean register, long registerID, String name, String psw, RequestHandler handler) {
        if (register) {
            return SDKClient.instance().getAuth().registerStep(registerID, name, psw, CallBackHelper.buildCallBack(handler));
        } else {
            return SDKClient.instance().getAuth().forgerPasswordStep(registerID, psw, CallBackHelper.buildCallBack(handler));
        }
    }

    // 删除聊天
    public static boolean deleteChat(long chatID) {
        return SDKClient.instance().getChatService().delete(chatID);
    }

    /**
     * 删除所有聊天
     *
     * @param handler
     * @return
     */
    public static boolean deleteAllChat(RequestHandler handler) {
        return SDKClient.instance().getChatService().clearAll(CallBackHelper.buildCallBack(handler));
    }

    /**
     * 清除当前群或个人所有聊天记录
     *
     * @param targetId
     * @param handler
     * @return
     */
    public static boolean deleteAllChat(long targetId, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().deleteAll(targetId, CallBackHelper.buildCallBack(handler));
    }

    public static boolean deleteMsgByID(long chatID, ArrayList<Long> deleteIDs) {
        return SDKClient.instance().getChatMsgService().deleteByID(chatID, deleteIDs, null);
    }

    public static boolean deleteMsgByTime(long chatID, long startTime, long endTime) {
        return SDKClient.instance().getChatMsgService().deleteByTime(chatID, startTime, endTime, null);
    }

    //设置系统消息已读
    public static boolean setSysMsgRead(ArrayList<SystemMsg> list) {
        return SDKClient.instance().getSysMsgService().setRead(list);
    }

    //获取历史聊天记录
    public static boolean getChatHistory(long targetID, long lastMsgID, int offset, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().getHistoryMsg(targetID, lastMsgID, offset, CallBackHelper.buildCallBack(handler));
    }

    //设置消息已读
    public static boolean setMsgRead(long targetID, long messageID) {
        return SDKClient.instance().getChatMsgService().setMsgRead(targetID, messageID);
    }

    //发送文本消息
    public static boolean sendTxt(long targetID, String text, ArrayList<Long> relatedUsers, RequestHandler handler) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        builder.createTxtMsg(text);
        builder.setRelatedUsers(relatedUsers);
        return SDKClient.instance().getChatMsgService().sendMsg(builder.build(), CallBackHelper.buildCallBack(handler));
    }

    //发送图片
    public static boolean sendImg(long targetID, String imgPath, RequestHandler handler) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        builder.createImageMsg(imgPath);
        return SDKClient.instance().getChatMsgService().sendMsg(builder.build(), CallBackHelper.buildCallBack(handler));
    }

    //发送文件
    public static boolean sendFile(long targetID, String filePath, RequestHandler handler) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        builder.createFileMsg(filePath);
        return SDKClient.instance().getChatMsgService().sendMsg(builder.build(), CallBackHelper.buildCallBack(handler));
    }

    //发送名片
    public static boolean sendCard(long targetID, long userID, RequestHandler handler) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        builder.createCardMsg(userID);
        return SDKClient.instance().getChatMsgService().sendMsg(builder.build(), CallBackHelper.buildCallBack(handler));
    }

    //发送位置
    public static boolean sendPosition(long targetID, String address, String latitude, String longitude, RequestHandler handler) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        builder.createPositionMsg(address, latitude, longitude);
        return SDKClient.instance().getChatMsgService().sendMsg(builder.build(), CallBackHelper.buildCallBack(handler));
    }

    /**
     * 发送语音
     *
     * @param targetID
     * @param audioPath
     * @param time      毫秒
     * @param handler
     * @return
     */
    public static boolean sendAudio(long targetID, String audioPath, int time, RequestHandler handler) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        builder.createAudioMsg(audioPath, time);
        return SDKClient.instance().getChatMsgService().sendMsg(builder.build(), CallBackHelper.buildCallBack(handler));
    }

    //发送图片
    public static boolean sendVideoRequest(long targetID, RequestHandler handler) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        builder.setMsgType(ChatMsgApi.TYPE_VIDEO);
        builder.setMessage("AAA");
        return SDKClient.instance().getChatMsgService().sendMsg(builder.build(), CallBackHelper.buildCallBack(handler));
    }

    //重发失败消息
    public static boolean reSend(ChatMsg chatMsg, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().sendMsg(chatMsg, CallBackHelper.buildCallBack(handler));
    }

    //设置标星
    public static boolean addStar(Contact contact, RequestHandler handler) {
        return contact.addStar(CallBackHelper.buildCallBack(handler));
    }

    //移除标星
    public static boolean removeStar(Contact contact, RequestHandler handler) {
        return contact.removeStar(CallBackHelper.buildCallBack(handler));
    }

    //设置好友聊天背景
    public static boolean setChatImage(Contact contact, String chatImage, RequestHandler handler) {
        return contact.setChatImage(chatImage, CallBackHelper.buildCallBack(handler));
    }

    //修改备注
    public static boolean modifyRemark(Contact contact, String remark, RequestHandler handler) {
        return contact.modifyRemark(remark, CallBackHelper.buildCallBack(handler));
    }

    //获取好友验证方式
    public static boolean getContactVerifyType(long userID, RequestHandler handler) {
        return SDKClient.instance().getContactService().getVerifyType(userID, CallBackHelper.buildCallBack(handler));
    }

    //添加好友
    public static boolean addContact(long userID, String verifyInfo, String remark, RequestHandler handler) {
        return SDKClient.instance().getContactService().addContact(userID, verifyInfo, remark, CallBackHelper.buildCallBack(handler));
    }

    //删除好友
    public static boolean removeContact(long userID, RequestHandler handler) {
        return SDKClient.instance().getContactService().removeContact(userID, CallBackHelper.buildCallBack(handler));
    }

    /**
     * 添加好友到黑名单
     *
     * @param blacks
     * @param handler
     * @return
     */
    public static boolean addBlack(ArrayList<Long> blacks, RequestHandler handler) {
        return SDKClient.instance().getContactService().addBlack(blacks, CallBackHelper.buildCallBack(handler));
    }

    /**
     * 从黑名单移除好友
     *
     * @param blacks
     * @param handler
     * @return
     */
    public static boolean removeBlack(ArrayList<Long> blacks, RequestHandler handler) {
        return SDKClient.instance().getContactService().removeBlack(blacks, CallBackHelper.buildCallBack(handler));
    }

    /**
     * 获取黑名单信息
     *
     * @param handler
     * @return
     */
    public static boolean getBlackInfo(RequestHandler handler) {
        return SDKClient.instance().getContactService().getBlackList(CallBackHelper.buildCallBack(handler));
    }

    /**
     * 隐藏好友
     *
     * @param password
     * @param list
     * @param handler
     * @return
     */
    public static boolean addHideContact(boolean hidden, String password, ArrayList<Long> list, RequestHandler handler) {
        return SDKClient.instance().getContactService().hiddenContact(hidden, password, list, CallBackHelper.buildCallBack(handler));
    }

    /**
     * 置顶聊天
     *
     * @param chatId  聊天id
     * @param isTop   是否设置为置顶
     * @param handler
     * @return
     */
    public static boolean topChat(long chatId, boolean isTop, RequestHandler handler) {
        return SDKClient.instance().getChatService().top(chatId, isTop, CallBackHelper.buildCallBack(handler));
    }


    //创建群
    public static boolean createGroup(ArrayList<Long> inviteIDs, RequestHandler handler) {
        return SDKClient.instance().getGroupService().createGroupByID(inviteIDs, CallBackHelper.buildCallBack(handler));
    }

    //转让群
    public static boolean transferGroup(Group group, long targetID, RequestHandler handler) {
        return group.transfer(targetID, CallBackHelper.buildCallBack(handler));
    }


    //获取群验证方式
    public static boolean getGroupVerifyType(long groupID, RequestHandler handler) {
        return SDKClient.instance().getGroupService().getVerifyType(groupID, CallBackHelper.buildCallBack(handler));
    }

    //申请加群
    public static boolean addGroup(long groupID, String verifyInfo, RequestHandler handler) {
        return SDKClient.instance().getGroupService().addGroup(groupID, verifyInfo, CallBackHelper.buildCallBack(handler));
    }

    //删除退出解散群
    public static boolean deleteGroup(Group group, RequestHandler handler) {
        return group.exit(CallBackHelper.buildCallBack(handler));
    }

    public static boolean modifyGroupInfo(Group oldGroup, Group newInfo, RequestHandler handler) {
        return oldGroup.modifyGroupInfo(newInfo, CallBackHelper.buildCallBack(handler));
    }

    public static boolean setGroupChatImageByMe(Group group, String chatImage, RequestHandler handler) {
        return group.setChatImageByMe(chatImage, CallBackHelper.buildCallBack(handler));
    }

    public static boolean setGroupChatImageByAdmin(Group group, String chatImage, RequestHandler handler) {
        return group.setChatImageByAdmin(chatImage, CallBackHelper.buildCallBack(handler));
    }

    public static boolean setGroupAvatar(Group group, String avatar, RequestHandler handler) {
        return group.modifyAvatar(avatar, CallBackHelper.buildCallBack(handler));
    }

    // 设置群消息提醒方式
    public static boolean setGroupMsgReminderType(Group group, byte msgSetType, String receiveTime, RequestHandler handler) {
        return group.setMsgReminderType(msgSetType, receiveTime, CallBackHelper.buildCallBack(handler));
    }

    // 获取群成员列表
    public static boolean getGroupMembers(Group group, RequestHandler handler) {
        return group.getMemberList(CallBackHelper.buildCallBack(handler));
    }

    // 添加群成员
    public static boolean addMembers(Group group, ArrayList<Long> inviteIDs, RequestHandler handler) {
        return group.addMemberListByID(inviteIDs, CallBackHelper.buildCallBack(handler));
    }

    // 移除群成员
    public static boolean removeMember(Group group, long removeID, RequestHandler handler) {
        return group.removeMember(removeID, CallBackHelper.buildCallBack(handler));
    }

    // 移除群成员列表
    public static boolean removeMembers(Group group, ArrayList<Long> members, RequestHandler handler) {
        return group.removeMemberList(members, CallBackHelper.buildCallBack(handler));
    }

    // 修改群成员名片
    public static boolean modifyMemberRemark() {
        return false;
    }

    // 下载缩略图
    public static boolean downloadThumbImg(ChatMsg chatMsg, RequestHandler handler) {
        return chatMsg.downloadThumbImg(CallBackHelper.buildCallBack(handler));
    }

    // 下载原图
    public static boolean downloadOrgImg(ChatMsg chatMsg, RequestHandler handler) {
        return chatMsg.downloadOrgImg(CallBackHelper.buildCallBack(handler));
    }

    // 下载文件
    public static boolean downloadFile(ChatMsg chatMsg, RequestHandler handler) {
        return chatMsg.downloadFile(CallBackHelper.buildCallBack(handler));
    }

    public static boolean uploadFile(String path, boolean encrypt, RequestHandler uploadHandler) {
        return false;
    }

    // 设置个人信息
    public static boolean setMyInfo(Contact contact, RequestHandler handler) {
        return SDKClient.instance().getAccountService().getCurrent().updateInfo(contact, CallBackHelper.buildCallBack(handler));
    }

    //修改设置头像
    public static boolean setAvatar(String avatar, RequestHandler handler) {
        return SDKClient.instance().getAccountService().getCurrent().setAvatar(avatar, CallBackHelper.buildCallBack(handler));
    }

    //获取陌生人信息
    public static boolean getUserInfo(long userID, RequestHandler handler) {
        return SDKClient.instance().getContactService().getInfo(userID, CallBackHelper.buildCallBack(handler));
    }

    //获取机器人信息
    public static boolean getAppInfo(long appID, RequestHandler handler) {
        return SDKClient.instance().getContactService().getAppInfo(appID, CallBackHelper.buildCallBack(handler));
    }

    //获取群信息
    public static boolean getGroupInfo(long groupID, RequestHandler handler) {
        return SDKClient.instance().getGroupService().getInfo(groupID, CallBackHelper.buildCallBack(handler));
    }

    //获取群成员信息
    public static boolean getMemberInfo(long memberID, RequestHandler handler) {
        return SDKClient.instance().getMemberService().getInfo(memberID, CallBackHelper.buildCallBack(handler));
    }

    /**
     * 获取企业列表
     *
     * @param handler
     * @return
     */
    public static boolean getEnterpriseList(RequestHandler handler) {
        return SDKClient.instance().getEnterpriseService().getEntList(CallBackHelper.buildCallBack(handler));
    }

    /**
     * 获取组织和用户列表
     *
     * @param enterpriseID
     * @param organizeID
     * @param handler
     * @return
     */
    public static boolean getOrgAndUserList(long enterpriseID, long organizeID, RequestHandler handler) {
        return SDKClient.instance().getEnterpriseService().getOrgAndUserList(enterpriseID, organizeID, CallBackHelper.buildCallBack(handler));
    }

    //网络搜索
    public static boolean searchNet(String key, RequestHandler handler) {
        return SDKClient.instance().getContactService().searchNet(key, CallBackHelper.buildCallBack(handler));
    }

    public static boolean searchLocal(String key, RequestHandler handler) {
        return SDKClient.instance().getContactService().searchLocal(key, CallBackHelper.buildCallBack(handler));
    }

    //系统消息
    public static boolean getSystemMsgList(long time, int offset, RequestHandler handler) {
        return SDKClient.instance().getSysMsgService().getList(time, offset, CallBackHelper.buildCallBack(handler));
    }

    //同意添加好友请求
    public static boolean responseContact(SystemMsg systemMsg, RequestHandler handler) {
        return SDKClient.instance().getSysMsgService().agreeContact(systemMsg.getId(), systemMsg.getUserID(), systemMsg.getName(), CallBackHelper.buildCallBack(handler));
    }

    //获取clientKey
    public static boolean getClientKey(RequestHandler handler) {
        return SDKClient.instance().getAuth().getClientKey(CallBackHelper.buildCallBack(handler));
    }

    /**
     * @param group
     * @param GroupMember
     * @param requestHandler
     * @return
     */
    public static boolean modifyGroupMemberInfo(Group group, GroupMember GroupMember, RequestHandler requestHandler) {
        return group.setMemberInfo(GroupMember, CallBackHelper.buildCallBack(requestHandler));
    }

    /**
     * 创建房间
     *
     * @param name
     * @param members
     * @param handler
     * @return
     */
    public static boolean createRoom(String name, ArrayList<Long> members, RequestHandler handler) {
        return SDKClient.instance().getChatService().createRoom(name, members, CallBackHelper.buildCallBack(handler));
    }

    public static boolean reNameRoom(long roomID, String name, RequestHandler handler) {
        return SDKClient.instance().getChatService().reNameRoom(roomID, name, CallBackHelper.buildCallBack(handler));
    }

    public static boolean deleteRoom(long roomID, RequestHandler handler) {
        return SDKClient.instance().getChatService().deleteRoom(roomID, CallBackHelper.buildCallBack(handler));
    }

    public static boolean getAllRooms(RequestHandler handler) {
        return SDKClient.instance().getChatService().getAllRooms(CallBackHelper.buildCallBack(handler));
    }

    public static boolean getHistoryTasks(RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().getHistoryTasks(CallBackHelper.buildCallBack(handler));
    }

    public static boolean getSendTasks(RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().getSendTasks(CallBackHelper.buildCallBack(handler));
    }

    public static boolean getReceiveTasks(RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().getReceiveTasks(CallBackHelper.buildCallBack(handler));
    }

    public static boolean getTaskBody(long msgID, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().getTaskBody(msgID, CallBackHelper.buildCallBack(handler));
    }

    public static boolean recoveryTask(long msgID, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().recoveryTask(msgID, CallBackHelper.buildCallBack(handler));
    }

    public static boolean finishTask(long msgID, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().finishTask(msgID, CallBackHelper.buildCallBack(handler));
    }

    public static boolean getSecUrl(String entArea, String version, RequestHandler handler) {
        return SDKClient.instance().getAuth().getSecUrl(entArea, version, CallBackHelper.buildCallBack(handler));
    }

    public static boolean getHiddenContact(String password, RequestHandler handler) {
        return SDKClient.instance().getContactService().getHiddenContact(password, CallBackHelper.buildCallBack(handler));
    }

    public static boolean getMsgsByType(long targetID, int msgType, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().getMsgsByType(targetID, msgType, CallBackHelper.buildCallBack(handler));
    }

    public static boolean getUrlInfo(String url, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().getUrlInfo(url, CallBackHelper.buildCallBack(handler));
    }

    public static boolean getServerTime(RequestHandler handler) {
        return SDKClient.instance().getAuth().getServerTime(CallBackHelper.buildCallBack(handler));
    }

    public static boolean verifyHiddenInfo(ArrayList<HiddenAccountInfoBean> list, RequestHandler handler) {
        return SDKClient.instance().getAuth().verifyHiddenInfo(list, CallBackHelper.buildCallBack(handler));
    }

    public static boolean resetHiddenPWD(String oldPwd, String newPwd, RequestHandler handler) {
        return SDKClient.instance().getAuth().resetHiddenPWD(oldPwd, newPwd, CallBackHelper.buildCallBack(handler));
    }

    public static boolean setPersonalData(ArrayList<PersonalDataBean> item, RequestHandler handler) {
        return SDKClient.instance().getAuth().setPersonalData(item, CallBackHelper.buildCallBack(handler));
    }

    public static boolean getPersonalData(ArrayList<PersonalDataBean> item, RequestHandler handler) {
        return SDKClient.instance().getAuth().getPersonalData(item, CallBackHelper.buildCallBack(handler));
    }

    public static boolean setUserSetting(int type, byte flag, RequestHandler handler) {
        return SDKClient.instance().getAuth().setUserSetting(type, flag, CallBackHelper.buildCallBack(handler));
    }

    public static boolean getUserSetting(int type, RequestHandler handler) {
        return SDKClient.instance().getAuth().getUserSetting(type, CallBackHelper.buildCallBack(handler));
    }

    public static boolean verifyUserSetting(int type, long resultValue) {
        return SDKClient.instance().getAuth().verifyUserSetting(type, resultValue);
    }

    public static boolean localSetting(byte type, ArrayList<LocalSettingBean> item, RequestHandler handler) {
        return SDKClient.instance().getAuth().localSetting(type, item, CallBackHelper.buildCallBack(handler));
    }

    public static boolean msgDetailSearch(long targetID, int count, int msgType, int msgID, String key, long startTime, long endTime, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().msgDetailSearch(targetID, count, msgType, msgID, key, startTime, endTime, CallBackHelper.buildCallBack(handler));
    }

    public static boolean msgGlobalSearch(long targetID, int count, int msgType, String key, long startTime, long endTime, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().msgGlobalSearch(targetID, count, msgType, key, startTime, endTime, CallBackHelper.buildCallBack(handler));
    }

    public static boolean getFileInfos(ArrayList<Long> fileMsgIds, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().getFileInfos(fileMsgIds, CallBackHelper.buildCallBack(handler));
    }

    public static boolean downloadFile(String localPath, String remotePath, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().downloadFile(localPath, remotePath, CallBackHelper.buildCallBack(handler));
    }

    public static boolean getShieldGroupMessage(Group group, RequestHandler handler) {
        return group.getMsgReminderType(CallBackHelper.buildCallBack(handler));
    }

    public static boolean getBuddyOnline(ArrayList<Long> buddyIds, RequestHandler handler) {
        return SDKClient.instance().getContactService().getOnline(buddyIds, CallBackHelper.buildCallBack(handler));
    }

    public static boolean faceToFaceCreateRoom(long userID, boolean isGroup, double latitude, double longitude, RequestHandler handler) {
        if (!isGroup)
            return SDKClient.instance().getContactService().faceToFaceCreateRoom(userID, latitude, longitude, CallBackHelper.buildCallBack(handler));
        else
            return SDKClient.instance().getGroupService().faceToFaceCreateRoom(userID, latitude, longitude, CallBackHelper.buildCallBack(handler));
    }

    public static boolean faceToFaceJoinRoom(long userID, boolean isGroup, double latitude, double longitude, String password, RequestHandler handler) {
        if (!isGroup)
            return SDKClient.instance().getContactService().faceToFaceJoinRoom(userID, latitude, longitude, password, CallBackHelper.buildCallBack(handler));
        else
            return SDKClient.instance().getGroupService().faceToFaceJoinRoom(userID, latitude, longitude, password, CallBackHelper.buildCallBack(handler));
    }

    public static boolean faceToFaceExit(boolean isGroup, long userId, String roomId, RequestHandler handler) {
        if (!isGroup)
            return SDKClient.instance().getContactService().faceToFaceExit(userId, roomId, CallBackHelper.buildCallBack(handler));
        else
            return SDKClient.instance().getGroupService().faceToFaceExit(userId, roomId, CallBackHelper.buildCallBack(handler));
    }

    public static boolean faceToFaceAddBuddy(long userId, String roomId, String verifyInfo, ArrayList<Long> userList, RequestHandler handler) {
        return SDKClient.instance().getContactService().faceToFaceAddBuddy(userId, roomId, verifyInfo, userList, CallBackHelper.buildCallBack(handler));
    }

    public static boolean faceToFaceCreateGroup(long userId, String roomId, byte groupLevel, ArrayList<Long> userList, RequestHandler handler) {
        return SDKClient.instance().getGroupService().faceToFaceCreateGroup(userId, roomId, groupLevel, userList, CallBackHelper.buildCallBack(handler));
    }

    public static boolean addNote(NoteInfoBean bean, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().addNote(bean, CallBackHelper.buildCallBack(handler));
    }

    public static boolean getAllNotes(long beginID, int offSet, byte offSetFlag, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().getAllNotes(beginID, offSet, offSetFlag, CallBackHelper.buildCallBack(handler));
    }

    public static boolean deleteNotes(ArrayList<Long> list, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().deleteNotes(list, CallBackHelper.buildCallBack(handler));
    }

    public static boolean editNote(NoteInfoBean bean, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().editNote(bean, CallBackHelper.buildCallBack(handler));
    }

    //绑定手机号第一步
    public static boolean bindPhone1(boolean bind, String phone, RequestHandler handler) {
        return SDKClient.instance().getAuth().bindPhoneStep1(bind, phone, CallBackHelper.buildCallBack(handler));
    }

    //绑定手机号第二步
    public static boolean bindPhone2(boolean bind, long registryID, String code, RequestHandler handler) {
        return SDKClient.instance().getAuth().bindPhoneStep2(bind, registryID, code, CallBackHelper.buildCallBack(handler));
    }

    //绑定手机号第三步
    public static boolean bindPhone3(boolean bind, long registryID, String code, RequestHandler handler) {
        return SDKClient.instance().getAuth().bindPhoneStep3(bind, registryID, code, CallBackHelper.buildCallBack(handler));
    }

    //绑定邮箱
    public static boolean bindEmail(boolean bind, String email, RequestHandler handler) {
        return SDKClient.instance().getAuth().bindEmail(bind, email, CallBackHelper.buildCallBack(handler));
    }

    //上传通讯录
    public static boolean postContacts(ArrayList<ContactBean> contacts, RequestHandler handler) {
        return SDKClient.instance().getContactService().postContacts(contacts, CallBackHelper.buildCallBack(handler));
    }

    public static boolean modifyPassword(String oldPassword, String newPassword, RequestHandler handler) {
        return SDKClient.instance().getAuth().modifyPassword(oldPassword, newPassword, CallBackHelper.buildCallBack(handler));
    }

    public static boolean importMsg(ArrayList<ChatMsg> list, RequestHandler handler) {
        return SDKClient.instance().getChatMsgService().importMsg(list, CallBackHelper.buildCallBack(handler));
    }
}
