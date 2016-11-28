package com.xwtech.bpe.logic.obsh.service.cds.zxrw;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.xwtech.bpe.framework.service.cds.CDSContext;
import com.xwtech.bpe.framework.service.cds.CDSResult;
import com.xwtech.bpe.framework.service.cds.IResultCode;
import com.xwtech.bpe.framework.util.CityUtil;
import com.xwtech.bpe.framework.util.DateTimeUtil;
import com.xwtech.bpe.framework.util.EscapeUtil;
import com.xwtech.bpe.framework.util.RequestUtil;
import com.xwtech.bpe.logic.common.service.IWorkOrderService;
import com.xwtech.bpe.logic.common.service.cds.BaseBusinessCDS;
import com.xwtech.bpe.logic.obsh.constants.LogicCodeConstants;
import com.xwtech.bpe.logic.obsh.constants.RequestConstants;
import com.xwtech.bpe.logic.obsh.constants.SystemCodeConstants;
import com.xwtech.bpe.logic.obsh.dao.IZxrwDAO;
import com.xwtech.bpe.logic.obsh.pojo.BusiLogBean;
import com.xwtech.bpe.logic.obsh.pojo.SalesUserInfoBean;
import com.xwtech.bpe.logic.obsh.pojo.UserInfoBean;
import com.xwtech.bpe.logic.obsh.pojo.ZxrwAreaInfoBean;
import com.xwtech.bpe.logic.obsh.pojo.ZxrwMarketInfoBean;
import com.xwtech.bpe.logic.obsh.pojo.ZxrwProductInfoBean;
import com.xwtech.bpe.logic.obsh.pojo.ZxrwProductRemarkBean;
import com.xwtech.bpe.logic.obsh.util.BusinessUtil;
import com.xwtech.xwecp.service.logic.IchkForNetByNameService;
import com.xwtech.xwecp.service.logic.LIException;
import com.xwtech.xwecp.service.logic.client_impl.common.ICreateHKOrderService;
import com.xwtech.xwecp.service.logic.client_impl.common.ICustomDataSubmitService;
import com.xwtech.xwecp.service.logic.client_impl.common.IQueryOrdersInfoService;
import com.xwtech.xwecp.service.logic.client_impl.common.IQueryqStatusByCardIdService;
import com.xwtech.xwecp.service.logic.client_impl.common.ITYZFService;
import com.xwtech.xwecp.service.logic.client_impl.common.ITelnumPickService;
import com.xwtech.xwecp.service.logic.pojo.BizInfoDt;
import com.xwtech.xwecp.service.logic.pojo.CwebCustInfoDt;
import com.xwtech.xwecp.service.logic.pojo.DEL050009Result;
import com.xwtech.xwecp.service.logic.pojo.DEL610090Result;
import com.xwtech.xwecp.service.logic.pojo.DeliveryInfoDt;
import com.xwtech.xwecp.service.logic.pojo.InvoiceInfoDt;
import com.xwtech.xwecp.service.logic.pojo.OrderInfoDt;
import com.xwtech.xwecp.service.logic.pojo.PaymentInfoDt;
import com.xwtech.xwecp.service.logic.pojo.ProIncrement;
import com.xwtech.xwecp.service.logic.pojo.ProPackage;
import com.xwtech.xwecp.service.logic.pojo.ProService;
import com.xwtech.xwecp.service.logic.pojo.QRY050038Result;
import com.xwtech.xwecp.service.logic.pojo.QRY050082Result;
import com.xwtech.xwecp.service.logic.pojo.TYZF00001Result;
import com.xwtech.xwecp.service.logic.pojo.Web4GFlexDt;

/**
 * 选号入网办理功能类
 * @author 丁亮
 * 开发日期:Jun 5, 2014 1:55:36 PM
 */
public class ZXRWShareNewTransECDS extends BaseBusinessCDS {
	Logger logger = Logger.getLogger(ZXRWShareNewTransECDS.class);
	
	private ICustomDataSubmitService customDataSubmitService;			// 在线入网客户资料提交接口
	private IQueryqStatusByCardIdService queryStatusByCardIdService;	// 身份证状态校验
	private IchkForNetByNameService chkForNetByNameService;				// 姓名校验
	private IWorkOrderService workOrderService;							// 订单接口
	private IZxrwDAO zxrwDAO;											// 在线入网数据库操作接口
	private ICreateHKOrderService createHKOrderService;					//订单中心接口
	private IQueryOrdersInfoService queryOrderInfoService;				//查看订单号是否同步CRM成功接口
	private ITYZFService tyzfService;									//订单支付接口
	private ITelnumPickService telnumPickService;						//锁号接口

	public ITelnumPickService getTelnumPickService() {
		return telnumPickService;
	}

	public void setTelnumPickService(ITelnumPickService telnumPickService) {
		this.telnumPickService = telnumPickService;
	}

	public ITYZFService getTyzfService() {
		return tyzfService;
	}

	public IQueryOrdersInfoService getQueryOrderInfoService() {
		return queryOrderInfoService;
	}

	public void setQueryOrderInfoService(
			IQueryOrdersInfoService queryOrderInfoService) {
		this.queryOrderInfoService = queryOrderInfoService;
	}

	public void setTyzfService(ITYZFService tyzfService) {
		this.tyzfService = tyzfService;
	}

	public IZxrwDAO getZxrwDAO() {
		return zxrwDAO;
	}

	public void setZxrwDAO(IZxrwDAO zxrwDAO) {
		this.zxrwDAO = zxrwDAO;
	}
	
	public IQueryqStatusByCardIdService getQueryStatusByCardIdService() {
		return queryStatusByCardIdService;
	}

	public void setQueryStatusByCardIdService(IQueryqStatusByCardIdService queryStatusByCardIdService) {
		this.queryStatusByCardIdService = queryStatusByCardIdService;
	}
	
	public ICustomDataSubmitService getCustomDataSubmitService() {
		return customDataSubmitService;
	}

	public void setCustomDataSubmitService(
			ICustomDataSubmitService customDataSubmitService) {
		this.customDataSubmitService = customDataSubmitService;
	}

	@Override
	public void doService(CDSContext cdsContext, CDSResult result) {
		String methed = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"methed", "");
		
		if("initZxrwMobileInfo".equals(methed)){
			initZxrwMobileInfo(cdsContext, result);
		}
		
		if("addShopCar".equals(methed) || "buyMobile".equals(methed)){
			addShopCar(cdsContext, result);
		}
		if("payBtn".equals(methed)){
			payCar(cdsContext, result);
		}
		if("querySidState".equals(methed)){
			querySidState(cdsContext, result);
		}
	}
	
	private void initZxrwMobileInfo(CDSContext cdsContext, CDSResult result){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List <BusiLogBean> busiLogList = new ArrayList<BusiLogBean>();
		
		String mobile = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"mobile", "");				// 获取号码
		String cityCode = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"cityCode", "");			// 获取地市
		String mobileMoney = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"mobileMoney", "");		// 获取金额
		String mobileOrg = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"mobileOrg", "");			// 获取区县编码
		
//		String submitType = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"submitType", "user");	// 提交类型 user：用户,sales：营业员
//		SalesUserInfoBean salesInfo = (SalesUserInfoBean)cdsContext.getRequest().getSession().getAttribute(RequestConstants.SALES_SESSION_INFO);
//		if(salesInfo != null && !salesInfo.getMobile().equals("")){
//			submitType = "sales";
//		}
		//调用锁号接口锁号---10分钟
		//号码归属地市
		String  region = CityUtil.cityNumForId(cityCode);
		//操作类型,参数值说明：当值为PICK时表示暂选操作；当值为UNPICK时表示释放操作
		String  operType = "PICK";
		//号码归属单位
		String  telorg = mobileOrg;
		//手机号码
		String  telnum = mobile;
		Map<String, Object> mapRet = new HashMap<String, Object>();
		mapRet.put("mobile", mobile);
		mapRet.put("login_msisdn", mobile);
		mapRet.put("loginiplock_login_ip", cdsContext.getRequest().getRemoteAddr());
		//mapRet.put("route_type", "1");
		//mapRet.put("route_value", region);
		mapRet.put("route_type", "2");
		mapRet.put("route_value", "15261441094");
		mapRet.put("ddr_city", region);
		mapRet.put("user_id", "0");

		this.generateContextForUnLogin(mapRet, "ZXRW_BUYMOBILE_SHARE", OPER_TYPE.OPER_TYPE_QUERY, cdsContext.getRequest());
			
			DEL610090Result ret;
			String isSetTime = null;
			Date sessionTime = null;	//倒计时
			
			try {
				sessionTime = ((Date) cdsContext.getRequest().getSession().getAttribute(telnum + "_sessionTime"));
				if(null == sessionTime){
					sessionTime = getSessionTime();
					cdsContext.getRequest().getSession().setAttribute(telnum + "_sessionTime",sessionTime);
				}
				isSetTime = ((String) cdsContext.getRequest().getSession().getAttribute(telnum + "_pickNumTime"));
				if(null == isSetTime || "".equals(isSetTime)){
					//查询号码是否被锁（占用）
					int isPick = zxrwDAO.queryZxrwTelnumUnpick(telnum);
					if(isPick > 0){
						resultMap.put("isPick", "1");
					}else{
						String nowTime = DateTimeUtil.getTodayChar14();
						isSetTime = nowTime;
						cdsContext.getRequest().getSession().setAttribute(telnum + "_pickNumTime",nowTime);
						ret = telnumPickService.telnumPick(region, operType, telorg, telnum);
						if(isResultSuccess(ret)){
							//锁号成功后记录号码和状态+时间（0：锁号；1：释放或已被购买）
							int flag = zxrwDAO.addZxrwTelnumUnpick(telnum,region,telorg,"0");
							result.setResultCode(IResultCode.CDS_HANDLE_SUCCESS);
						}
					}
				}
			} catch (Exception e1) {
				logger.error(e1,e1);
				result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
				e1.printStackTrace();
			}
		try {
			// ===================== 获取当前时间返回 =====================
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			String nowTime = sdf.format(new Date());
//			resultMap.put("nowTime", nowTime);
			
			resultMap.put("submitType", "user");
			resultMap.put("sessionTime", DateTimeUtil.getOracleDate(sessionTime));
			String nowSeesionTime = DateTimeUtil.getTodayChar14();
			resultMap.put("nowTime",nowSeesionTime);
//			String cityId = "";
//			NumResource numResource = zxrwDAO.getNumResource(mobile);
//			if(numResource == null){
//				result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
//				result.setLogicCode(LogicCodeConstants.ZXRW_NEW_NOTMOBILE_ERR);
//			}
//			
//			BusiLogBean numResourceLog = new BusiLogBean();
//			numResourceLog.setOperType(OPER_TYPE_LOG.CHANGE);
//			numResourceLog.setSystemCode(numResource == null ? SystemCodeConstants.ZXRW_CARD_ERROR : "0");
//			numResourceLog.setLogicCode(numResource == null ? LogicCodeConstants.ZXRW_NEW_NOTMOBILE_ERR : "0");
//		    busiLogList.add(numResourceLog);
//			
//			// > 校验Session有效性、数量、号码时间是否超时
//			this.checkedSessionMobile(cdsContext, result, mobile, busiLogList, resultMap);
//			cityId = numResource.getCityId();
			String cityId = CityUtil.cityNumForId(cityCode);
			resultMap.put("mobile", mobile);
			resultMap.put("money", mobileMoney);
			resultMap.put("mobileOrg", mobileOrg);
			resultMap.put("cityName", CityUtil.cityNumToName(cityCode));
			resultMap.put("cityId", cityId);
			
			// ===================== 获取所有品牌套餐 =========================
			List<ZxrwProductInfoBean> szxProducts = new ArrayList<ZxrwProductInfoBean>();
			List<ZxrwProductInfoBean> qqtProducts = new ArrayList<ZxrwProductInfoBean>();
			List<ZxrwProductInfoBean> dgddProducts = new ArrayList<ZxrwProductInfoBean>();
			List<ZxrwProductInfoBean> lteProducts = new ArrayList<ZxrwProductInfoBean>();
			List<ZxrwProductInfoBean> llktcProducts = new ArrayList<ZxrwProductInfoBean>();
			
			List<ZxrwProductInfoBean> zxrwProductInfos = zxrwDAO.queryZxrwProByCityByMem(cityId);
			BusiLogBean zxrwProductInfoLog = new BusiLogBean();
			zxrwProductInfoLog.setOperType(OPER_TYPE_LOG.CHANGE);
			zxrwProductInfoLog.setSystemCode(zxrwProductInfos == null ? SystemCodeConstants.ZXRW_NEW_ERR : "0");
			zxrwProductInfoLog.setLogicCode(zxrwProductInfos == null ? LogicCodeConstants.ZXRW_NEW_PRODUCTREMARK_ERR : "0");
		    busiLogList.add(zxrwProductInfoLog);
			
			if(zxrwProductInfos != null){
			    for (ZxrwProductInfoBean productInfo : zxrwProductInfos) {
					if("SZX".equals(productInfo.getBrandCode())){
						szxProducts.add(productInfo);
					}
					
					if("QQT".equals(productInfo.getBrandCode())){
						qqtProducts.add(productInfo);
					}
					
					if("DGDD".equals(productInfo.getBrandCode())){
						dgddProducts.add(productInfo);
					}
					
					if("LET".equals(productInfo.getBrandCode())){
						lteProducts.add(productInfo);
					}
					
					if("LLKTC".equals(productInfo.getBrandCode())){
						llktcProducts.add(productInfo);
					}
				}
			}
			resultMap.put("productListSZX", szxProducts);
			resultMap.put("productListQQT", qqtProducts);
			resultMap.put("productListDGDD", dgddProducts);
			resultMap.put("productListLET", lteProducts);
			resultMap.put("productListLLKTC", llktcProducts);

			// ===================== 获取所有套餐资费信息 =====================
			List<ZxrwProductRemarkBean> productRemarks = zxrwDAO.queryZxrwProductRemarkByMem();
			resultMap.put("productRemarkList", productRemarks);
			BusiLogBean productRemarksLog = new BusiLogBean();
			productRemarksLog.setOperType(OPER_TYPE_LOG.CHANGE);
			productRemarksLog.setSystemCode(zxrwProductInfos == null ? SystemCodeConstants.ZXRW_NEW_ERR : "0");
			productRemarksLog.setLogicCode(zxrwProductInfos == null ? LogicCodeConstants.ZXRW_NEW_PRODUCTREMARK_ERR : "0");
		    busiLogList.add(productRemarksLog);
			
			// ===================== 获取所有营销案列表 =====================
			List<ZxrwMarketInfoBean> marketInfos = zxrwDAO.queryMarketInfos(cityId,"0");
			resultMap.put("marketList", marketInfos);
			BusiLogBean marketInfosLog = new BusiLogBean();
			marketInfosLog.setOperType(OPER_TYPE_LOG.CHANGE);
			marketInfosLog.setSystemCode(zxrwProductInfos == null ? SystemCodeConstants.ZXRW_NEW_ERR : "0");
			marketInfosLog.setLogicCode(zxrwProductInfos == null ? LogicCodeConstants.ZXRW_NEW_PRODUCTREMARK_ERR : "0");
		    busiLogList.add(marketInfosLog);
			
			result.setResultObj(resultMap);
			result.setResultCode(IResultCode.CDS_HANDLE_SUCCESS);
		} catch (Exception e) {
			logger.error(e, e);
			result.setSystemCode(IResultCode.CDS_HANDLE_FAILED);
		} finally{
			UserInfoBean userInfo = BusinessUtil.getUser(cdsContext.getRequest());
			if(userInfo != null){
				generateLogParam(cdsContext, "", "", "", "", "", "ZXRW_MOBILEINFO_SHARE", mobile);
			} else {
				generateLogParamNoLogin(cdsContext, "", "", "", "", "", "ZXRW_MOBILEINFO_SHARE", mobile);
			}
			
        	setLogParaValForKey(cdsContext,RequestConstants.MORE_WRITE_BUSILOG, busiLogList);
		}
	}
	
	private Date getSessionTime(){
		Date time = null;
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date afterDate = new Date(now .getTime() + 600000);
		time = afterDate;
		System.out.println(sdf.format(afterDate));
		return time;
		
	}
	
	private void querySidState(CDSContext cdsContext, CDSResult result){
		String busiNum = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"busiNum", "");									// 业务编码
		String ddzxSid = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"ddzxSid", "");									// 订单号
		String saleMobileMoney = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"saleMobileMoney", "");					// 金额
		String saleMobile = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"saleMobile", "");							// 选择的号码
		String contactTel = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"contactTel", "");							// 联系号码
		try{
			//查看订单中心的订单号同步CRM是否成功
			String cunstInfoCreateTime = DateTimeUtil.getTodayChar14();
			Map<String,String> paramMapqry = new HashMap<String,String>();
			paramMapqry.put("testFlag", "0");
			paramMapqry.put("version", "0.1");
			paramMapqry.put("serviceCode", "10000");
			paramMapqry.put("templateCode", "TMPL_ORDER_FETCH_COMM");
			paramMapqry.put("pageIndex", "1");
			paramMapqry.put("pageSize", "5");
			paramMapqry.put("sTime", "20151028000000");
			paramMapqry.put("eTime", cunstInfoCreateTime);
			paramMapqry.put("orderType", "");//宽带预约时候这个参数要是填了值会报错
			paramMapqry.put("status", "");
			paramMapqry.put("orderFrom", "");
			paramMapqry.put("payStatus", "");
			paramMapqry.put("deliveryType", "");
			paramMapqry.put("userId", "");
			paramMapqry.put("orderId", ddzxSid);
			paramMapqry.put("accNbr", "");//这里宽带预约订单查询的时候要不填
			paramMapqry.put("icNo", "");
			
			//订单中心接口调用
			Map<String, Object> mapRet = new HashMap<String, Object>();
			mapRet.put("mobile", saleMobile);
			mapRet.put("login_msisdn", saleMobile);
			mapRet.put("loginiplock_login_ip", cdsContext.getRequest().getRemoteAddr());
			//mapRet.put("route_type", "1");
			//mapRet.put("route_value", "14");
			mapRet.put("route_type", "1");
			mapRet.put("route_value", "15261441094");
			mapRet.put("ddr_city", "14");
			mapRet.put("user_id", "0");

			this.generateContextForUnLogin(mapRet, busiNum, OPER_TYPE.OPER_TYPE_QUERY, cdsContext.getRequest());
			String state = "";
			QRY050082Result retCrm = queryOrderInfoService.queryOrdersInfo(paramMapqry);
			if(isResultSuccess(retCrm)){
				state = retCrm.getOrderInfoList().get(0).getStatus();
				//-1 未同步成功 20同步成功
				if("20".equals(state)){
					result.setResultCode(IResultCode.CDS_HANDLE_SUCCESS);
				}else{
					result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
				}
			}
		}catch(LIException e){
			e.printStackTrace();
			logger.error(e, e);
			result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e, e);
			result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
		}finally{
			UserInfoBean userInfo = BusinessUtil.getUser(cdsContext.getRequest());
			
			String param1 = ddzxSid + "==|=="+ saleMobileMoney +"==|==" + saleMobile + "==|==" + contactTel + "===|===SidQuery";
			
			if(userInfo != null){
				generateLogParam(cdsContext, param1);
			} else {
				generateLogParamNoLogin(cdsContext, param1);
			}
			
		}
	}
	
	private void payCar(CDSContext cdsContext, CDSResult result) {
//		String lockSessionId = cdsContext.getRequest().getSession(true).getId();
		String busiNum = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"busiNum", "");									// 业务编码
		String ddzxSid = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"ddzxSid", "");									// 订单号
		String saleMobileMoney = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"saleMobileMoney", "");					// 金额
		String saleMobile = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"saleMobile", "");							// 选择的号码
		String contactTel = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"contactTel", "");							// 联系号码
		String createTime = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"createTime", "");							// 下单时间
		
		String sessionId = "";
		try{
			// 手机号、预留字段 
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("apiid", "2222000");
			paramMap.put("userid", contactTel);
			
			//收银台支付
			//"apiid", "2222000"是用来号卡标准流程，充值类营销案支付
			paramMap.put("orderno",ddzxSid);
			paramMap.put("totalamount",saleMobileMoney + "00");
			paramMap.put("period","30");
			paramMap.put("periodunit","4");//2日；3：时；4：分
			//调用支付中心接口，只有此处参数为号码：
			//1、目前传的是联系人号码，应该是此处导致支付页面显示“充值手机号：139xxx”；
			//2：如果传购买的号码 接口会报错
			paramMap.put("mblno",contactTel);
			paramMap.put("userid","");
			paramMap.put("paycnl","4");
			paramMap.put("bankcnl","");
			paramMap.put("orderdesc","");
			paramMap.put("notifyurl","");
			paramMap.put("bustyp","0");//下单的时候业务类型必输，1缴话费，0其他业务，支付中心这边用这个发通知
			paramMap.put("reserved","");
			
			
			//查看订单中心的订单号同步CRM是否成功
			String cunstInfoCreateTime = DateTimeUtil.getTodayChar14();
			Map<String,String> paramMapqry = new HashMap<String,String>();
			paramMapqry.put("testFlag", "0");
			paramMapqry.put("version", "0.1");
			paramMapqry.put("serviceCode", "10000");
			paramMapqry.put("templateCode", "TMPL_ORDER_FETCH_COMM");
			paramMapqry.put("pageIndex", "1");
			paramMapqry.put("pageSize", "5");
			paramMapqry.put("sTime", "20151028000000");
			paramMapqry.put("eTime", cunstInfoCreateTime);
			paramMapqry.put("orderType", "");//宽带预约时候这个参数要是填了值会报错
			paramMapqry.put("status", "");
			paramMapqry.put("orderFrom", "");
			paramMapqry.put("payStatus", "");
			paramMapqry.put("deliveryType", "");
			paramMapqry.put("userId", "");
			paramMapqry.put("orderId", ddzxSid);
			paramMapqry.put("accNbr", "");//这里宽带预约订单查询的时候要不填
			paramMapqry.put("icNo", "");
			
			//判断下单时间是否超过30分钟
			String betweenTime = "0";
			if(!"".equals(createTime) && null != createTime){
				betweenTime = DateTimeUtil.getDistanceDT(createTime,cunstInfoCreateTime,"m");
			}
			if(Integer.parseInt(betweenTime) < 31){
				//订单中心接口调用
				Map<String, Object> mapRet = new HashMap<String, Object>();
				mapRet.put("mobile", saleMobile);
				mapRet.put("login_msisdn", saleMobile);
				mapRet.put("loginiplock_login_ip", cdsContext.getRequest().getRemoteAddr());
				//mapRet.put("route_type", "1");
				//mapRet.put("route_value", "14");
				mapRet.put("route_type", "1");
				mapRet.put("route_value", "15261441094");
				mapRet.put("ddr_city", "14");
				mapRet.put("user_id", "0");

				this.generateContextForUnLogin(mapRet, busiNum, OPER_TYPE.OPER_TYPE_QUERY, cdsContext.getRequest());
				String state = "";
//				Thread.sleep(2000);
				QRY050082Result retCrm = queryOrderInfoService.queryOrdersInfo(paramMapqry);
				if(isResultSuccess(retCrm)){
					state = retCrm.getOrderInfoList().get(0).getStatus();
					//-1 未同步成功
					if("-1".equals(state)){
						//过2秒再次调用查询接口
//						Thread.sleep(2000);
						QRY050082Result retCrmRe = queryOrderInfoService.queryOrdersInfo(paramMapqry);
						state = retCrmRe.getOrderInfoList().get(0).getStatus();
						if("-1".equals(state)){
							result.setLogicCode("100001");	//自定义：订单同步CRM不成功OR其他状态
							result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
						}
					}
					if("20".equals(state)){
						//QRY050082成功的话再调支付中心接口获取令牌值
						TYZF00001Result ret = tyzfService.tyzfMethod(paramMap);
						if(isResultSuccess(ret)){
							sessionId = ret.getCashPayment().getSessionid();
							result.setResultObj(sessionId);
							result.setResultCode(IResultCode.CDS_HANDLE_SUCCESS);
//							/**> 锁号码,"6":锁10天 */
//							boolean lockedMobile = zxrwDAO.lockedMobile(saleMobileMoney, lockSessionId, "6", IPUtil.getIpAddr(cdsContext.getRequest()));
//							if(!lockedMobile){
//								result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
//								result.setLogicCode(LogicCodeConstants.ZXRW_NEW_LOCKED_ERR);
//							}
						}else{
							result.setLogicCode("100004");	//自定义：获取令牌值失败
							result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
						}
					}
					
					
//					if("20".equals(state)){
//						//QRY050082成功的话再调支付中心接口获取令牌值
//						TYZF00001Result ret = tyzfService.tyzfMethod(paramMap);
//						if(isResultSuccess(ret)){
//							sessionId = ret.getCashPayment().getSessionid();
//							result.setResultObj(sessionId);
//							result.setResultCode(IResultCode.CDS_HANDLE_SUCCESS);
//							/**> 锁号码,"6":锁10天 */
//							boolean lockedMobile = zxrwDAO.lockedMobile(saleMobileMoney, lockSessionId, "6", IPUtil.getIpAddr(cdsContext.getRequest()));
//							if(!lockedMobile){
//								result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
//								result.setLogicCode(LogicCodeConstants.ZXRW_NEW_LOCKED_ERR);
//							}
//						}else{
//							result.setLogicCode("100004");	//自定义：获取令牌值失败
//							result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
//						}
//					}
//					else{
//						result.setLogicCode("100001");	//自定义：订单同步CRM不成功OR其他状态
//						result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
//					}
				}else{
					result.setLogicCode("100003");	//自定义：订单同步CRM接口调用失败
					result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
				}
			}else{
				result.setLogicCode("100002");	//自定义：订单未支付超过30分钟
				result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
			}
			
			
		}catch(LIException e){
			e.printStackTrace();
			logger.error(e, e);
			result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e, e);
			result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
		}finally{
			UserInfoBean userInfo = BusinessUtil.getUser(cdsContext.getRequest());
			
			String param1 = ddzxSid + "==|=="+ saleMobileMoney +"==|==" + saleMobile + "==|==" + contactTel + "===|===" + sessionId;
			
			if(userInfo != null){
				generateLogParam(cdsContext, param1);
			} else {
				generateLogParamNoLogin(cdsContext, param1);
			}
			
		}

		
	}

	/**
	 * =======================================================
	 * 开发日期：2012-11-7
	 * 开发人：丁亮
	 * 方法说明：针对加入购物车或立即购买的业务逻辑操作
	 * =======================================================
	 * @param cdsContext
	 * @param result
	 */
	@SuppressWarnings("unchecked")
	private void addShopCar(CDSContext cdsContext, CDSResult result){
		String sessionId = cdsContext.getRequest().getSession(true).getId();
		List <BusiLogBean> busiLogList = new ArrayList<BusiLogBean>();
		String verifyCode = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"verifyCode", "");
		if (verifyCode == null || !verifyCode.equalsIgnoreCase((String) cdsContext.getRequest().getSession(true).getAttribute("zxrw"))) {
			result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
			result.setLogicCode("verifyCodeWrong");
			return;
		}
		
		String submitType = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"submitType", "");							// 提交类型 user:用户, sales:营业员
		
		// 如果是营业员登录，则取金额、地市
		String salesCityId = "";
		String salesFee = "";
//		if(submitType.equals("sales")){
			salesCityId = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"salesCityId", "");
			salesFee = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"salesFee", "");
//		}
		
		String busiNum = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"busiNum", "");									// 业务编码
		String mobile = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"mobile", "");									// 获取号码
		String mobileOrg = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"salesMobileOrg", "");							// 号码所属区县编码
		String zxrwUserName = EscapeUtil.unescape(RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"username", ""));		// 获取姓名
		String zxrwUserCard = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"usercard", "");							// 获取身份证号
		String zxrwUserAddres = EscapeUtil.unescape(RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"cardAddress", ""));	// 获取身份证所在地址
		String zxrwContactTel = EscapeUtil.unescape(RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"contactTel", ""));	// 联系人号码
		String sendCardType = EscapeUtil.unescape(RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"sendCardType", ""));	// 取卡方式
		//寄送地址：
		String deliveryAddr = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"deliveryAddr", "");
		String zxrwLoginType = "";
		
		//判断选号时间是否超过10分钟
		String betweenTime = "0";
		String selectNumTime = (String) cdsContext.getRequest().getSession(true).getAttribute(mobile + "_pickNumTime");
		String createTime = DateTimeUtil.getTodayChar14();
		if(!"".equals(createTime) && null != createTime){
			betweenTime = DateTimeUtil.getDistanceDT(selectNumTime,createTime,"m");
		}
		if(Integer.parseInt(betweenTime) >= 10){
			result.setLogicCode("-4056");
			result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
			return;
		}
		
		// 校验长度
		if(checkStr(zxrwUserName,"1") || checkStr(zxrwUserCard, "2") || checkStr(zxrwUserAddres, "3")){
			result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
			result.setLogicCode(LogicCodeConstants.ZXRW_NEW_STR_LENGTH_ERR);
			return;
		}
		
		// > 转换品牌编码
		String zxrwBrandCode = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"brandId", "");							// 获取品牌编码
		if(zxrwBrandCode.equals("QQT")){
			zxrwBrandCode = "QQT_QQT";
		}
		else if(zxrwBrandCode.equals("SZX")){
			zxrwBrandCode = "SZX_BZK";
		}
		else if(zxrwBrandCode.equals("DGDD")){
			zxrwBrandCode = "DGDD_DGDD2";
		}
		
		String zxrwPkgCode =  "";																										// 主体产品编码
		String productTypeId = "";																										// 套餐分类编码
		String zxrwProductCode =  RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"productId", "");						// 套餐编码
		
		// 必选业务编码,来电显示:2100000013,数据：2100000007、省内漫游：2100000003、呼叫转移：2100000009、
		// 呼叫等待：2100000010、呼叫保持：2100000015、省际漫游：2100000002、多方通话：2100000012、短信：2100000004
		String zxrwServiceCode = "2100000013|来电显示,2100000007|数据,2100000003|省内漫游,2100000009|呼叫转移,2100000010|呼叫等待,2100000015|呼叫保持,2100000002|省际漫游,2100000012|多方通话,2100000004|短信";																				
		String orderIds = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"orderIds", "");								// 获取流水号码
//		String tmpZxrwIncrementCode = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"incrementIds", "");				// 获取可选业务编码组
//		String zxrwIncrementCode = setIncrementCodes(tmpZxrwIncrementCode);
		
		String zxrwIncrementCode = "2200005025|CMNET,2200005026|CMWAP";
		
		String cityId = "";		// 地市ID
		String countyId = "";	// 区县ID
		String cityName = "";	// 地市名称
		String countyName = "";	// 区县名称
		String cityCode = "";	// 地市编码
		String countyCode = "";	// 区县编码
		String newZxrwProdcut = "";
		
		String zxrwMarketId = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"marketId", "");							// 获取营销案编码;
		
		String zxrwMarketLevel = RequestUtil.getStringValueFromRequest(cdsContext.getRequest(),"marketLevelId", "");
		if(zxrwMarketId == null || zxrwMarketId.equals("null") || zxrwMarketId.equals("undefined")){
			zxrwMarketId = "";
		}
		
		if(zxrwMarketLevel == null || zxrwMarketLevel.equals("null") || zxrwMarketLevel.equals("undefined")){
			zxrwMarketLevel = "";
		}
		
		// 根据营销案编码获取营销案相关信息
		String zxrwMarketName = "";		// 营销案名称
		String zxrwMarketFee = "0";		// 营销案金额
		String zxrwMarketPackId = "";	// 营销案业务包编码
		String zxrwMarketWardId = "";	// 营销案礼品包编码
		String zxrwMobileMoney = "";																									// 号码金额
		String SID = "";																												// 生成的流水号
		String ddzxSid = "";	//订单中心订单号
		String productName = "";//产品名称
		
		try {
			/* ==========================================================================================
			 * 号码只可购买一次
			 * ========================================================================================== */
//			List<ZxrwMobileSession> sessionMobiles = null;
//			NumResource numResource = null;
//			if(submitType.equals("user")){
//				sessionMobiles = (List<ZxrwMobileSession>) cdsContext.getRequest().getSession().getAttribute("USER_ZXRW_MOBILE");
//				if(sessionMobiles != null){
//					// 防止已购买过重复提交
//					for (ZxrwMobileSession zxrwMobileSession : sessionMobiles) {
//						// > 校验session里的号码与选择的号码匹配
//						if(zxrwMobileSession.getMobile().equals(mobile)){
//							// 防止信息提交后后退到详情页里的提示
//							if(zxrwMobileSession.getSid() != null && !zxrwMobileSession.getSid().equals("")){
//								result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
//								result.setLogicCode(LogicCodeConstants.ZXRW_NEW_MOBILE_LOCEK_ERR);
//								return;
//							}
//						}
//					}
//				}
//				
//				numResource = zxrwDAO.getNumResource(mobile);
//				if(numResource == null){
//					result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
//					result.setLogicCode(LogicCodeConstants.ZXRW_NEW_NOTMOBILE_ERR);
//					return;
//				}
//			}
			
//			/**> 获取号码的真实金额*/
//			zxrwMobileMoney = (submitType.equals("user") ? numResource.getFeeNeed() : salesFee);
//			
//			/**
//			 * 取号码所属地市
//			 * 如果号码与地市不正确是无法正确的提交选号入网的
//			 * */
//			cityId = (submitType.equals("user") ? numResource.getCityId() : salesCityId);
			zxrwMobileMoney = salesFee;
			cityId = salesCityId;
			countyId = getCountyId(salesCityId);
			
			// 获取号码所属的地市名称
			ZxrwAreaInfoBean cityAreaInfo = zxrwDAO.queryAreaInfo(cityId);
			ZxrwAreaInfoBean countyAreaInfo = zxrwDAO.queryAreaInfo(countyId);
			cityName = (cityAreaInfo == null ? "" : cityAreaInfo.getFAreaName());
//			countyName = (countyAreaInfo == null ? "" : countyAreaInfo.getFAreaName());
			countyName = zxrwDAO.queryMobileOrgCity(mobileOrg);
			cityCode = (cityAreaInfo == null ? "" : cityAreaInfo.getFAreaNum());
			countyCode = (countyAreaInfo == null ? "" : countyAreaInfo.getFAreaNum());
			
			ZxrwProductInfoBean productInfoBean = zxrwDAO.queryZxrwProductInfoForProIdByMem(cityId, zxrwProductCode);
			if(productInfoBean != null){
				productTypeId = productInfoBean.getProductTypeId();
				productName = productInfoBean.getProductName();
				zxrwPkgCode = productInfoBean.getProductChanPing();
				newZxrwProdcut += zxrwProductCode + "|" + (productTypeId == null ? "" : productTypeId) + "|" + productName + ",";
			}
			
			if(!newZxrwProdcut.equals("") && newZxrwProdcut.lastIndexOf(",") != -1){
				newZxrwProdcut = newZxrwProdcut.substring(0,newZxrwProdcut.length() - 1);
			}
			
			// 校验营销案编码真实性
			List<ZxrwMarketInfoBean> marketInfoBean = zxrwDAO.queryMarketInfos(cityId, zxrwProductCode, "0");
			boolean marketFlag = false;
			if(marketInfoBean != null && marketInfoBean.size() > 0){
				for (ZxrwMarketInfoBean zxrwMarketInfo : marketInfoBean) {
					String _marketId = zxrwMarketInfo.getMarketId();
					String _marketLevelId = zxrwMarketInfo.getMarketLevel();
					if(_marketId.equals(zxrwMarketId) && _marketLevelId.equals(zxrwMarketLevel)){
						zxrwMarketName = zxrwMarketInfo.getMarketName();
						zxrwMarketFee = zxrwMarketInfo.getMarketFee();
						zxrwMarketPackId = zxrwMarketInfo.getMarketPackId();
						zxrwMarketWardId = zxrwMarketInfo.getMarketWardId();
						marketFlag = true;
						continue;
					}
				}
				
				// 校验营销案是否真实
				if(marketFlag == true && ((zxrwMarketId == null && zxrwMarketId.equals("")) || (zxrwMarketLevel == null && zxrwMarketLevel.equals("")))){
					result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
					result.setLogicCode(LogicCodeConstants.ZXRW_NEW_MARKET_ERR);
					return;
				}
			}
			else {
				zxrwMarketId = "";
				zxrwMarketLevel = "";
				zxrwMarketFee = "0";
				zxrwMarketPackId = "";
				zxrwMarketWardId = "";
			}
			
			if(!zxrwMarketPackId.equals("null") && !zxrwMarketPackId.equals("")){
				zxrwPkgCode += "," + zxrwMarketPackId;
			}
			
			if(!zxrwMarketWardId.equals("null") && !zxrwMarketWardId.equals("")){
				zxrwPkgCode += "," + zxrwMarketWardId;
			}
			
			// 根据返回的流水号校验是否有重复的身份证号
			boolean cardIdState = zxrwDAO.queryZxrwOrderInfoForCardIdState(orderIds, zxrwUserCard);
			if(cardIdState){
				result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
				result.setLogicCode(LogicCodeConstants.ZXRW_NEW_CARDID_ERR);
				return;
			}
			
			/** ===================================================================================
			 * > 调取CRM身份证校验接口，判断是否满5个号码，是否有预约号码
			 * > 目前只有返回失败并且错误编码是101的时候才认为是欠费用户，其他暂不做考虑
			 * > 校验身份证码返回的编码为超出预约限制
			 */
			String tmpCity = cityId;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mobile", mobile);
			map.put("login_msisdn", mobile);
			map.put("loginiplock_login_ip", cdsContext.getRequest().getRemoteAddr());
			//map.put("route_type", "1");
			//map.put("route_value", tmpCity);
			map.put("route_type", "1");
			map.put("route_value", "15261441094");
			map.put("ddr_city", tmpCity);
			map.put("user_id", "0");

			this.generateContextForUnLogin(map, busiNum, OPER_TYPE.OPER_TYPE_QUERY, cdsContext.getRequest());
			QRY050038Result retCard = queryStatusByCardIdService.queryStatusByCardId("1", zxrwUserCard);
			//>..... 记录操作日志
			BusiLogBean cardLog = new BusiLogBean();
			cardLog.setOperType(OPER_TYPE_LOG.QUERY);
			
			// 校验身份证号码
			if(!isResultSuccess(retCard)){
				result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
				result.setLogicCode(LogicCodeConstants.ZXRW_NEW_CARD_COUNT_ERR);
				result.setResultObj(retCard.getErrorMessage());
				cardLog.setSystemCode(retCard.getErrorCode());
				busiLogList.add(cardLog);
				return;
			}
//			if("101".equals(retCard.getErrorCode()) || "10001".equals(retCard.getErrorCode()) || "10002".equals(retCard.getErrorCode()) || "10003".equals(retCard.getErrorCode())){
//				result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
//				result.setLogicCode(LogicCodeConstants.ZXRW_NEW_CARD_ERR);
//				cardLog.setSystemCode(retCard.getErrorCode());
//				busiLogList.add(cardLog);
//				return;
//			}
			cardLog.setSystemCode(IResultCode.CDS_HANDLE_SUCCESS);
			busiLogList.add(cardLog);
			
			// 校验用户身份证姓名 ================ 华为接口有问题，暂时屏蔽 =========================
//			BusiLogBean chkNameLog = new BusiLogBean();
//			chkNameLog.setOperType(OPER_TYPE_LOG.QUERY);
//			
//			this.generateContextForUnLogin(map, busiNum, OPER_TYPE.OPER_TYPE_QUERY, cdsContext.getRequest());
//			QRY050077Result retChkName = chkForNetByNameService.queryStatusByCardId("1", zxrwUserCard, zxrwUserName);
//			if(retChkName.getBookId().equals("1") || retChkName.getBookId().equals("101") || retChkName.getBookId().equals("102")){
//				result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
//				if(retChkName.getBookId().equals("101")){
//					result.setLogicCode(LogicCodeConstants.ZXRW_NEW_CARD_NAME_COUNT_ERR);
//				}
//				
//				if(retChkName.getBookId().equals("1") || retChkName.getBookId().equals("102")){
//					result.setLogicCode(LogicCodeConstants.ZXRW_NEW_CARD_NAME_ERR);
//				}
//				
//				cardLog.setSystemCode(retCard.getErrorCode());
//				busiLogList.add(cardLog);
//				return;
//			}
//			chkNameLog.setSystemCode(IResultCode.CDS_HANDLE_SUCCESS);
//			busiLogList.add(chkNameLog);
			
			
			// == 盐城地区 == 神州行必须开通彩铃
			if(cityId.equals("22") && zxrwBrandCode.equals("SZX_BZK")){
				zxrwIncrementCode += ",2200005006|彩铃";
			}
			// == 盐城地区 == 神州行必须开通彩铃
			
			// == 泰州地区 == 神州行18元卡套餐必须开通彩铃和咪咕特级会员
			if(cityId.equals("21") && zxrwProductCode.equals("2000003201")){
				zxrwIncrementCode += ",2200005006|彩铃,2400000262|6元咪咕特级会员";
			}
			// == 泰州地区 == 神州行18元卡套餐必须开通彩铃和咪咕特级会员
			
			// == 镇江地区 == 
			if(cityId.equals("18")){
				if(mobile.equals("15951270688") || mobile.equals("15951271288") || mobile.equals("15951275388") || mobile.equals("15951281788") || mobile.equals("15951283788")){
					zxrwIncrementCode += ",2000004545|最低消费100元";
				}
				
				if(mobile.equals("15706107038") || mobile.equals("15706107138") || mobile.equals("15706107178") || mobile.equals("15706106568") || mobile.equals("15706106598")){
					zxrwIncrementCode += ",2000004543|最低消费30元";
				}
			}
			// == 镇江地区 == 
			
			// 如果是LTE就要开通4G功能
			if(zxrwBrandCode.equals("LET")){
				zxrwIncrementCode += ",2400000280|4G功能";
				
				
				// 苏州必须开通两个业务
				if(cityId.equals("11")){
					zxrwIncrementCode += ",2400000262|6元咪咕特级会员";
				}
			}
			
			// 如果是选择充200元送游戏手柄营销案，必须开通游戏玩家5元版业务
			if((zxrwMarketId.equals("3001638035") && zxrwMarketLevel.equals("300002422080")) || 
			            (zxrwMarketId.equals("3001092021") && zxrwMarketLevel.equals("300002216018")) || 
			            (zxrwMarketId.equals("3001636036") && zxrwMarketLevel.equals("300002422078")) ||
			            (zxrwMarketId.equals("3000666029") && zxrwMarketLevel.equals("300000894085")) ||
			            (zxrwMarketId.equals("2523101128") && zxrwMarketLevel.equals("300002230018")) ||
			            (zxrwMarketId.equals("3001634037") && zxrwMarketLevel.equals("300002420080")) ||
			            (zxrwMarketId.equals("3001080020") && zxrwMarketLevel.equals("300002528030")) ||
			            (zxrwMarketId.equals("3001648035") && zxrwMarketLevel.equals("300002438078")) ||
			            (zxrwMarketId.equals("3001090023") && zxrwMarketLevel.equals("300002214023")) ||
			            (zxrwMarketId.equals("3000174015") && zxrwMarketLevel.equals("300002224019")) ||
			            (zxrwMarketId.equals("3000516019") && zxrwMarketLevel.equals("300002510031")) ||
			            (zxrwMarketId.equals("3001312019") && zxrwMarketLevel.equals("300002492030"))){
				zxrwIncrementCode += ",2380000024|5元游戏玩家";
			}
			
			/**> 添加订单信息(网厅订单）*/
			SID = String.valueOf(UUID.randomUUID());
			
			//创建时间
			zxrwPkgCode = "1000100305";
			String orderCreateTime = DateTimeUtil.getTodayChar6();
			String cunstInfoCreateTime = DateTimeUtil.getTodayChar14();
			/**准备参数 start*/
			OrderInfoDt orderInfoDt = new OrderInfoDt();
			orderInfoDt.setBookingId("");//空
			orderInfoDt.setUserId(""); //空
			orderInfoDt.setOrderType("1");//1:订购 4:预约(宽带)
			orderInfoDt.setOfferId(""); //产品(套餐)编码	"2000003198"------********--------
			orderInfoDt.setOfferName(""); //产品编码（选购号码）	"2000003198(15189662750)"----------********-------
			orderInfoDt.setMarketSaleId(""); //营销案档次编码	"300001778076"
			orderInfoDt.setMarketSaleName(""); //营销案名称	"选号送50元话费"
			orderInfoDt.setAmount(Integer.parseInt(zxrwMobileMoney) + "00"); //金额	5000
			orderInfoDt.setDiscountAmount("0");//折扣金额
			orderInfoDt.setFactAmount(Integer.parseInt(zxrwMobileMoney) + "00"); //实际金额	5000
			orderInfoDt.setPayStatus("0"); //支付状态  0：未支付  1：已支付
			orderInfoDt.setDeliveryType(sendCardType);	//"WXTQ"
			orderInfoDt.setInvoiceFlag("1"); //是否需要发票  0：否 1：是
			orderInfoDt.setCreateTime(cunstInfoCreateTime); //创建时间	"20150730173000"
			orderInfoDt.setRemark(""); //备注  "订单备注信息"
			orderInfoDt.setSiteName(""); //营业厅名称 	"取货营业厅名称"
			
			
			PaymentInfoDt paymentInfoDt =new PaymentInfoDt();
			paymentInfoDt.setPayType("ZXZF");//在线支付
			paymentInfoDt.setPayMode("QEZF");//全额支付
			paymentInfoDt.setPayAmount(Integer.parseInt(zxrwMobileMoney) + "00");//支付金额 分
			paymentInfoDt.setPayTime(cunstInfoCreateTime); ////支付时间	"20150629101000"
			paymentInfoDt.setPaySerialNo("");//支付流水号
			paymentInfoDt.setPayOrgId(""); //支付机构编码
			paymentInfoDt.setPayOrgName("");//支付机构名称

			
			DeliveryInfoDt deliveryInfoDt =new DeliveryInfoDt();//网选厅取 以下应该可以都为空
			deliveryInfoDt.setPostCode(""); //邮编
			deliveryInfoDt.setDeliveryAddr(deliveryAddr);//寄送地址
			deliveryInfoDt.setDeliveryPeriod(""); //客户收货时间段描述   :工作日 周末之类
			deliveryInfoDt.setDeliveryName(zxrwUserName); //收货人
			deliveryInfoDt.setDeliveryPhone(zxrwContactTel); //收货人号码
			
			
			InvoiceInfoDt invoiceInfoDt =new InvoiceInfoDt();//网选厅取 以下应该可以都为空 
			invoiceInfoDt.setInvoiceDetail("");//发票内容
			invoiceInfoDt.setInvoiceTitle("");//发票抬头
			invoiceInfoDt.setInvoiceType("1");//发票类型 //1：普通发票	2：增值发票
			
			BizInfoDt bizInfoDt =new BizInfoDt();
			bizInfoDt.setCityId(cityId); //地市编码	"12"
			bizInfoDt.setCustName(zxrwUserName);//客户姓名
			bizInfoDt.setIcType("IdCard");//证件类型
			bizInfoDt.setIcNo(zxrwUserCard);//证件号码
			bizInfoDt.setIcAddr(zxrwUserAddres); //证件地址
			bizInfoDt.setCustAddr(zxrwUserAddres); //联系人地址 暂时可和证件地址一样
			bizInfoDt.setContactName(zxrwUserName); //联系人
			bizInfoDt.setContactPhone(zxrwContactTel);//联系人号码
			bizInfoDt.setAccNbr(mobile); //选购号码
			bizInfoDt.setSimCode("");//后加的一个节点 空
			
			//提交4G自选套餐实体类Web4GFlexDt
			List<Web4GFlexDt> web4Gflex = new ArrayList<Web4GFlexDt>();
			for (int i = 0;i<2;i++){
				Web4GFlexDt flexdt = new Web4GFlexDt();
					if(i == 0) {
						flexdt.setWeb4Goperatingsrl("");
						flexdt.setWeb4Gflexpkgcode("2000007352");
						flexdt.setWeb4Gflexpkgstartdate("2016-11-07 14:08:00");
						flexdt.setWeb4Gflexpkgenddate("2100-12-31 01:00:00");
						flexdt.setWeb4Gflexpkgmsisdn("1");
						flexdt.setWeb4GflexPpkgcode("2400000291");
						}
					if(i == 1) {
						flexdt.setWeb4Goperatingsrl("");
						flexdt.setWeb4Gflexpkgcode("2000007351");
						flexdt.setWeb4Gflexpkgstartdate("2016-11-07 14:08:00");
						flexdt.setWeb4Gflexpkgenddate("2100-12-31 01:00:00");
						flexdt.setWeb4Gflexpkgmsisdn("2");
						flexdt.setWeb4GflexPpkgcode("2400000295");
					}
					
					web4Gflex.add(flexdt);
		}
			
			//提交在线入网资料实体类CwebCustInfoDt
			CwebCustInfoDt cwebcustinfodt = new CwebCustInfoDt();
			cwebcustinfodt.setWebCustInfoSiteName(getUserCityName(cityCode));//ShopCartUtil.getUserCityName(zb.getCityCode())
			cwebcustinfodt.setWebCustInfoCreateTime(cunstInfoCreateTime); //创建时间	"20150608"
			cwebcustinfodt.setWebCustInfoSiteId(mobileOrg);//ShopCartUtil.getUserCityNameCode(zb.getCityCode())
			cwebcustinfodt.setWebCustInfoFetchFlag(sendCardType);//这里需要写死成"WXTQ",否则撤销订单功能无法正常使用
			cwebcustinfodt.setWebCustInfoIcAddr(zxrwUserAddres); //身份证地址
			cwebcustinfodt.setWebCustInfoCustName(zxrwUserName); //用户姓名
			cwebcustinfodt.setWebCustInfoMarketId("");  //营销案编码	"3001060165"
			cwebcustinfodt.setWebCustInfoMarketLevelId(""); //营销案档次编码	"300001516226"
			if("0".equals("")){
				cwebcustinfodt.setMarketFee(""); //营销费用
			}else{
				cwebcustinfodt.setMarketFee("00"); //营销案费用
			}
			cwebcustinfodt.setWebCustInfoAmount(Integer.parseInt("50")+"00");//总费用
			cwebcustinfodt.setWebCustInfoIcNo(zxrwUserCard);//身份证号码
			cwebcustinfodt.setWebCustInfoTel(zxrwContactTel);//联系人号码
			cwebcustinfodt.setWebCustInfoMsisdn(mobile); //选购号码
			cwebcustinfodt.setWebCustInfoSiteAddr(""); //营业厅地址	"江苏移动通信有限责任公司淮安分公司"
			cwebcustinfodt.setWebCustInfoCustAddr(zxrwUserAddres);//联系人地址
			cwebcustinfodt.setWebCustInfoPostCode("");//邮政编码
			cwebcustinfodt.setWebCustInfoEmsNo(""); //ems 传空
			cwebcustinfodt.setWebCustInfoBrandId(zxrwBrandCode);////品牌编码	LET
			cwebcustinfodt.setWebCustInfoWebBookingId(""); //空
			cwebcustinfodt.setWebCustInfoCityId(cityId); //地市编码
			cwebcustinfodt.setWebCustInfoCountryId(getCountyId(cityId)); //区县编码
			cwebcustinfodt.setWebCustInfoIcType("IdCard"); //证件类型
			cwebcustinfodt.setWebCustInfoProductId("1000100305");//产品编码	"100"  ------******-------
			cwebcustinfodt.setWebCustInfoReceivableFee(Integer.parseInt(zxrwMobileMoney) + "00"); //应收金额
			
			cwebcustinfodt.setWebCustInfoMsisdnPickFlag("1");	//号码状态	0. 未锁定	1. 已经锁定
			
			cwebcustinfodt.setWebCustInfoOpenMarket(""); //空
			cwebcustinfodt.setWebCustInfoOperatingSrl("");//空
			cwebcustinfodt.setWebCustInfoOperSource("9"); 
			cwebcustinfodt.setWebCustInfoStatus("1");
			cwebcustinfodt.setMarketBusiPackId("");
			cwebcustinfodt.setMarketGoodsPackId("");
			cwebcustinfodt.setWebCustInfoOtherMarket("");
			cwebcustinfodt.setWebCustInfoHrn("");
			cwebcustinfodt.setWebCustInfoPayType("1"); //支付类型 //支付类型1-银联卡  2-省内充值卡； 3-支付宝"
			
			
			List<ProPackage> proPackages = new ArrayList<ProPackage>();
//			for (int i = 0;i<1;i++){
//				ProPackage pckBean = new ProPackage();
//				if(i == 0) {pckBean.setPkgId(zxrwProductCode);pckBean.setTypeId(productTypeId);}
//				pckBean.setPkgLevel("3");
//				proPackages.add(pckBean);
//			}
			//组装附加功能的list
			List<ProService> proServices = new ArrayList<ProService>();
			for (int i = 0;i<9;i++){
				ProService serviceBean = new ProService();
				if(i == 0) {serviceBean.setServiceId("2100000013");}
				if(i == 1) {serviceBean.setServiceId("2100000007");}
				if(i == 2) {serviceBean.setServiceId("2100000003");}
				if(i == 3) {serviceBean.setServiceId("2100000009");}
				if(i == 4) {serviceBean.setServiceId("2100000010");}
				if(i == 5) {serviceBean.setServiceId("2100000015");}
				if(i == 6) {serviceBean.setServiceId("2100000002");}
				if(i == 7) {serviceBean.setServiceId("2100000012");}
				if(i == 8) {serviceBean.setServiceId("2100000004");}
				proServices.add(serviceBean);
			}
			
			//组装增值业务的list
			List<ProIncrement> proIncrements = new ArrayList<ProIncrement>();
			for (int i = 0;i<2;i++){
				ProIncrement incBean = new ProIncrement();
				if(i == 0) {incBean.setIncrementId("2200005025");}
				if(i == 1) {incBean.setIncrementId("2200005026");}
				
				proIncrements.add(incBean);
			}
			/**参数准备 end*/
			
			//订单中心接口调用
			Map<String, Object> mapRet = new HashMap<String, Object>();
			mapRet.put("mobile", mobile);
			mapRet.put("login_msisdn", mobile);
			mapRet.put("loginiplock_login_ip", cdsContext.getRequest().getRemoteAddr());
			//mapRet.put("route_type", "1");
			//mapRet.put("route_value", cityId);
			mapRet.put("route_type", "2");
			mapRet.put("route_value", "15261441094");
			mapRet.put("ddr_city", cityId);
			mapRet.put("request_source", "102");//渠道区分
			mapRet.put("user_id", "0");

			this.generateContextForUnLogin(mapRet, busiNum, OPER_TYPE.OPER_TYPE_QUERY, cdsContext.getRequest());
			
//			//调解锁接口
//			//号码归属地市
//			String  region = cityId;
//			//操作类型,参数值说明：当值为PICK时表示暂选操作；当值为UNPICK时表示释放操作
//			String  operType = "UNPICK";
//			//号码归属单位
//			String  telorg = mobileOrg;
//			//手机号码
//			String  telnum = mobile;
//			Map<String, Object> mapReg = new HashMap<String, Object>();
//			mapReg.put("mobile", mobile);
//			mapReg.put("login_msisdn", mobile);
//			mapReg.put("loginiplock_login_ip", cdsContext.getRequest().getRemoteAddr());
//			mapReg.put("route_type", "1");
//			mapReg.put("route_value", region);
//			mapReg.put("ddr_city", region);
//			mapReg.put("user_id", "0");
//
//				DEL610090Result retReg;
//				try {
//					retReg = telnumPickService.telnumPick(region, operType, telorg, telnum);
//					if(isResultSuccess(retReg)){
//						result.setResultCode(IResultCode.CDS_HANDLE_SUCCESS);
//					}
//				} catch (LIException e1) {
//					logger.error(e1,e1);
//					result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
//					e1.printStackTrace();
//				}
			DEL050009Result ret = createHKOrderService.createHkOrder("0", "0.2", "10000", "TMPL_ORDER_CREATE_HKXS", orderInfoDt, paymentInfoDt, 
																	deliveryInfoDt, invoiceInfoDt, bizInfoDt,cityId,cunstInfoCreateTime,"1_99","",
																	"2","###outorderid###","",mobile,web4Gflex,cwebcustinfodt,proPackages, proServices, proIncrements);
			System.out.println("-----------------dms------------resultCode:"+ret.getResultCode());
			if (isResultSuccess(ret)){
				ddzxSid = ret.getOrderId();
				/**> 下单成功锁号码,"2":锁24小时 */
//				boolean lockedMobile = zxrwDAO.lockedMobile(mobile, sessionId, "2", IPUtil.getIpAddr(cdsContext.getRequest()));
				//>..... 记录操作日志类型为变更
				BusiLogBean lockedLog = new BusiLogBean();
			    lockedLog.setOperType(OPER_TYPE_LOG.CHANGE);
//			    lockedLog.setSystemCode(lockedMobile == true ? SystemCodeConstants.ZXRW_NEW_ERR : "0");
//			    lockedLog.setLogicCode(lockedMobile == true ? LogicCodeConstants.ZXRW_NEW_LOCKED_ERR : "0");
			    busiLogList.add(lockedLog);
			    
//				if(!lockedMobile){
//					result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
//					result.setLogicCode(LogicCodeConstants.ZXRW_NEW_LOCKED_ERR);
//					return;
//				}
			}else{
				
				//订单中心生成订单失败
				result.setSystemCode(SystemCodeConstants.ZXRW_NEW_CREATEID_ERROR);
				result.setLogicCode(LogicCodeConstants.ZXRW_NEW_CREATEID_ERR);
				return;
			}
			
			boolean orderInfo = zxrwDAO.addZxrwOrderInfo(SID, mobile, zxrwMobileMoney, zxrwUserName, zxrwUserCard, zxrwUserAddres, zxrwBrandCode, zxrwPkgCode, 
					newZxrwProdcut, zxrwServiceCode, zxrwIncrementCode, zxrwMarketId, zxrwMarketLevel, zxrwMarketFee, 
					zxrwMarketName, cityId, cityCode, cityName, countyCode, countyName, submitType,sendCardType,ddzxSid,zxrwContactTel,mobileOrg);
			//>..... 记录操作日志类型为变更
			BusiLogBean orderInfoLog = new BusiLogBean();
			orderInfoLog.setOperType(OPER_TYPE_LOG.OPEN);
			orderInfoLog.setSystemCode(orderInfo == true ? "0" : SystemCodeConstants.ZXRW_NEW_ERR);
			orderInfoLog.setLogicCode(orderInfo == true ? "0" : LogicCodeConstants.ZXRW_NEW_ORDERINFO_ERR);
		    busiLogList.add(orderInfoLog);
		    
			if(!orderInfo){
				result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
				result.setLogicCode(LogicCodeConstants.ZXRW_NEW_ORDERINFO_ERR);
				return;
			}
			
//			boolean lockedSID = zxrwDAO.lockedSID(mobile, sessionId, SID);
			//>..... 记录操作日志类型为变更
			BusiLogBean lockedSIDLog = new BusiLogBean();
			lockedSIDLog.setOperType(OPER_TYPE_LOG.CHANGE);
//			lockedSIDLog.setSystemCode(lockedSID == true ? IResultCode.CDS_HANDLE_SUCCESS : SystemCodeConstants.ZXRW_NEW_ERR);
//			lockedSIDLog.setLogicCode(lockedSID == true ? "0" : LogicCodeConstants.ZXRW_NEW_ORDERLOCKED_ERR);
//		    busiLogList.add(lockedSIDLog);
//		    if(!lockedSID){
//				result.setSystemCode(SystemCodeConstants.ZXRW_NEW_ERR);
//				result.setLogicCode(LogicCodeConstants.ZXRW_NEW_ORDERLOCKED_ERR);
//				return;
//			}
		    
		    // 用户类型处理号码session数量控制
//		    if(submitType.equals("user")){
//		    	if(sessionMobiles != null && sessionMobiles.size() > 0){
//		    		for (ZxrwMobileSession zxrwMobileSession : sessionMobiles) {
//		    			if(zxrwMobileSession.getMobile().equals(mobile)){
//		    				zxrwMobileSession.setSid(SID);
//		    				continue;
//		    			}
//		    		}
//		    	}
//		    }
			
			Map<String,Object> resultMap = new HashMap<String,Object>();
			resultMap.put("SID", SID);
			//订单号
			resultMap.put("ddzxSid", ddzxSid);
			//金额
			resultMap.put("saleMobileMoney", zxrwMobileMoney);
			//订单商品（号码
			resultMap.put("saleMobile", mobile);
			//所选区县
			resultMap.put("saleCountyName", countyName);
			//下单时间
			resultMap.put("createTime", cunstInfoCreateTime);
			
			// 如果是营业员登录则提交入网资料
			if(submitType.equals("sales")){
				Map<String, String> salesMap = new HashMap<String, String>();
				Object objSales = cdsContext.getRequest().getSession(true).getAttribute(RequestConstants.SALES_SESSION_INFO);
				SalesUserInfoBean salesUserInfoBean = (SalesUserInfoBean) objSales;
				String salesNumber = salesUserInfoBean.getWorkNumber();
				String salesName = salesUserInfoBean.getUserName();
				String salesForCityNum = salesUserInfoBean.getCityNum();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String createDateTime = sdf.format(new Date());
				String amount = "";
				if(Integer.parseInt(zxrwMobileMoney) > Integer.parseInt(zxrwMarketFee)){
					amount = zxrwMobileMoney;
				}
				else if(Integer.parseInt(zxrwMobileMoney) == Integer.parseInt(zxrwMarketFee)){
					amount = zxrwMobileMoney;
				}
				else{
					amount = zxrwMarketFee;
				}
				
				salesMap.put("F_ORDER_TYPE", "2");								// 工单类型
				salesMap.put("F_MOBILE_NUM", mobile);							// 选择的号码
				salesMap.put("F_SALER_WORKNUM", salesNumber);					// 营业员工号							
				salesMap.put("F_SALER_NAME", salesName);						// 营业员姓名
				salesMap.put("F_SALER_HALLNUM", "");							// 营业员编码
				salesMap.put("F_SALER_CITY", salesForCityNum);					// 营业员所属地区
				salesMap.put("F_START_TIME", createDateTime);					// 工单发起时间
				salesMap.put("F_TRAN_WORKNUM", "");								// 办理员工号
				salesMap.put("F_TRAN_NAME", "");								// 办理员姓名
				salesMap.put("F_TRAN_HALLNUM", "");								// 办理员编码
				salesMap.put("F_TRAN_CITY", "");								// 办理员营业厅所在地市
				salesMap.put("F_END_TIME", "");									// 工单办理时间
				salesMap.put("F_ORDER_STATUS", "0");							// 工单状态
				salesMap.put("F_ORDER_AMOUNT", amount);							// 工单金额
				salesMap.put("F_SERIAL_NUMBER", SID);							// 流水号
				
				// 提交工单
				workOrderService.insertWorkOrder(salesMap);
				
				// ===========(重要)============
				// 提交资料成功后删除号码池里的号码
//				zxrwDAO.delLockedNum(mobile);
				// ===========================
			}
			
			result.setResultObj(resultMap);
			result.setResultCode(IResultCode.CDS_HANDLE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e, e);
			result.setSystemCode(SystemCodeConstants.PROXY_PLATFORM_EXCEPTION);
			result.setResultCode(IResultCode.CDS_HANDLE_FAILED);
		} finally{
			UserInfoBean userInfo = BusinessUtil.getUser(cdsContext.getRequest());
			
			String param1 = SID + "==|=="+ zxrwUserName +"==|==" + zxrwUserCard + "==|==" + zxrwUserAddres;					// 流水号,用户姓名,身份证号
			String param2 = mobile + "==|==" + zxrwMobileMoney + "==|==" + cityCode;										// 购买的号码,号码金额,地市,用户身份证所在地址
			String param3 = zxrwBrandCode + "==|==" + zxrwPkgCode + "==|==" + newZxrwProdcut;								// 品牌编码,产品编码
			String param4 = zxrwMarketName + "==|==" +zxrwMarketId + "==|==" + zxrwMarketLevel + "==|==" +zxrwMarketFee;	// 营销案编码,营销案二级编码,营销案名称,营销案金额
			String param5 = zxrwLoginType;
			String param6 = "ZXRW_BUYMOBILE_INFO_SUBMIT";																	// 立即购买号码和放入购物车
			String param7 = mobile;																							// 购买的号码
			
			if(userInfo != null){
				generateLogParam(cdsContext, param1, param2, param3, param4, param5, param6, param7);
			} else {
				generateLogParamNoLogin(cdsContext, param1, param2, param3, param4, param5, param6, param7);
			}
			
        	setLogParaValForKey(cdsContext,RequestConstants.MORE_WRITE_BUSILOG, busiLogList);
		}
	}

	public ICreateHKOrderService getCreateHKOrderService() {
		return createHKOrderService;
	}

	public void setCreateHKOrderService(ICreateHKOrderService createHKOrderService) {
		this.createHKOrderService = createHKOrderService;
	}

	private String getUserCityNameCode(String param) {
		if (param == null)
        {
            return "";
        }
        if (param.equals("SZDQ"))
        {
            return "11181127";
        }
        else if (param.equals("HADQ"))
        {
            return "12179859";//12149903
        }
        else if (param.equals("SQDQ"))
        {
            return "13810193";
        }
        else if (param.equals("NJDQ"))
        {
            return "14600544";
        }
        else if (param.equals("LYGDQ"))
        {
            return "15770219";
        }
        else if (param.equals("XZDQ"))
        {
            return "16790168";
        }
        else if (param.equals("CZDQ"))
        {
            return "17420073";
        }
        else if (param.equals("ZJDQ"))
        {
            return "18881174";
        }
        else if (param.equals("WXDQ"))
        {
            return "19500349";
        }
        else if (param.equals("NTDQ"))
        {
            return "20900513";
        }
        else if (param.equals("TZDQ"))
        {
            return "21761263";
        }
        else if (param.equals("YCDQ"))
        {
            return "22700521";
        }
        else if (param.equals("YZDQ"))
        {
            return "23800055";
        }
        else
        {
            return "";
        }
	}

	private String getUserCityName(String param) {
		if (param == null)
        {
            return "";
        }
        if (param.equals("SZDQ"))
        {
            return "苏州市终端互联网销售虚拟厅";
        }
        else if (param.equals("HADQ"))
        {
            return "淮安终端虚拟营业厅";
        }
        else if (param.equals("SQDQ"))
        {
            return "宿迁终端互联网销售虚拟营业厅";
        }
        else if (param.equals("NJDQ"))
        {
            return "南京市终端互联网销售虚拟厅";
        }
        else if (param.equals("LYGDQ"))
        {
            return "连云港终端互联网销售虚拟营业厅";
        }
        else if (param.equals("XZDQ"))
        {
            return "徐州终端虚拟营业厅";
        }
        else if (param.equals("CZDQ"))
        {
            return "常州市终端互联网销售虚拟厅";
        }
        else if (param.equals("ZJDQ"))
        {
            return "镇江市终端互联网销售虚拟厅";
        }
        else if (param.equals("WXDQ"))
        {
            return "无锡终端虚拟厅";
        }
        else if (param.equals("NTDQ"))
        {
            return "南通终端虚拟营业厅";
        }
        else if (param.equals("TZDQ"))
        {
            return "泰州市终端互联网销售虚拟厅";
        }
        else if (param.equals("YCDQ"))
        {
            return "盐城市终端互联网销售虚拟厅";
        }
        else if (param.equals("YZDQ"))
        {
            return "扬州终端虚拟营业厅";
        }
        else
        {
            return "";
        }
	}

	private String setIncrementCodes(String tmpZxrwIncrementCode) {
		// 必选业务编码CMNET:2200005025,CMWAP:2200005026
		String []zxrwIncrementCodeChe = {"2200005006", "2200005000", // 彩铃、短信呼
				"2000007048", "2000007049", "2000007050", "2000003877", "2000003809", "2000003779","2000003780", "2000003781","2000003782", "2000003783","2000003815",  // 通用流量包编码
				"2000002565", "2000002566", "2000002567", "2000002568", // 短信包
				"2400000001", "2400000005", "2200005013", "2000004650", "2380000063",
				"2380000024", "2300660001", "2400000262", "2380000145",
				"2000003758", "2000003759", "2000003760", "2000003761", "2000003762", // 新增4G业务
				"2000003766", "2000003767", "2000003768", "2000003769", "2000003770", "2000003771",	// 新增4G业务
				"2000003784", "2000003797"	// 新增4G业务
				};
		String zxrwIncrementCode = "2200005025|CMNET,2200005026|CMWAP,";
		if(tmpZxrwIncrementCode != null && !tmpZxrwIncrementCode.equals("")){
			for (String incCode : zxrwIncrementCodeChe) {
				if(tmpZxrwIncrementCode.indexOf(incCode) != -1){
					if(incCode.equals("2200005006")){
						zxrwIncrementCode += incCode + "|彩铃,";
					}
					
					if(incCode.equals("2200005000")){
						zxrwIncrementCode += incCode + "|短信呼,";
					}
					
					if(incCode.equals("2000007048")){
						zxrwIncrementCode += incCode + "|5元通用流量包,";
					}
					
					if(incCode.equals("2000007049")){
						zxrwIncrementCode += incCode + "|10元通用流量包,";
					}
					
					if(incCode.equals("2000007050")){
						zxrwIncrementCode += incCode + "|20元通用流量包,";
					}
					
					if(incCode.equals("2000003877")){
						zxrwIncrementCode += incCode + "|30元通用流量包,";
					}
					
					if(incCode.equals("2000003809")){
						zxrwIncrementCode += incCode + "|40元通用流量包,";
					}
					
					if(incCode.equals("2000003779")){
						zxrwIncrementCode += incCode + "|50元通用流量包,";
					}
					
					if(incCode.equals("2000003780")){
						zxrwIncrementCode += incCode + "|70元通用流量包,";
					}
					
					if(incCode.equals("2000003781")){
						zxrwIncrementCode += incCode + "|100元通用流量包,";
					}
					
					if(incCode.equals("2000003782")){
						zxrwIncrementCode += incCode + "|130元通用流量包,";
					}
					
					if(incCode.equals("2000003783")){
						zxrwIncrementCode += incCode + "|180元通用流量包,";
					}
					
					if(incCode.equals("2000003815")){
						zxrwIncrementCode += incCode + "|280元通用流量包,";
					}
					
					if(incCode.equals("2000002565")){
						zxrwIncrementCode += incCode + "|短信10元包150条,";
					}
					
					if(incCode.equals("2000002566")){
						zxrwIncrementCode += incCode + "|短信20元包300条,";
					}
					
					if(incCode.equals("2000002567")){
						zxrwIncrementCode += incCode + "|短信30元包500条,";
					}
					
					if(incCode.equals("2000002568")){
						zxrwIncrementCode += incCode + "|短信50元包800条,";
					}
					
					if(incCode.equals("2000004650")){
						zxrwIncrementCode += incCode + "|5元彩信套餐,";
					}

					if(incCode.equals("2380000063")){
						zxrwIncrementCode += incCode + "|手机阅读(悦读会),";
					}
					
					/**WLAN业务*/
					if(incCode.equals("2000003800")){
						zxrwIncrementCode += incCode + "|WLAN流量资费5元套餐,";
					}
					
					if(incCode.equals("2000003801")){
						zxrwIncrementCode += incCode + "|WLAN流量资费10元套餐,";
					}
					
					if(incCode.equals("2000003802")){
						zxrwIncrementCode += incCode + "|WLAN流量资费20元套餐,";
					}
					
					if(incCode.equals("2000003803")){
						zxrwIncrementCode += incCode + "|WLAN流量资费30元套餐,";
					}
					
					if(incCode.equals("2000003804")){
						zxrwIncrementCode += incCode + "|WLAN流量资费50元套餐,";
					}
					
					if(incCode.equals("2000003805")){
						zxrwIncrementCode += incCode + "|WLAN流量资费100元套餐,";
					}
					
					if(incCode.equals("2400000005")){
						zxrwIncrementCode += incCode + "|飞信(免费版),";
					}
					
					if(incCode.equals("2400000001")){
						zxrwIncrementCode += incCode + "|139邮箱(免费版),";
					}
					
					if(incCode.equals("2200005013")){
						zxrwIncrementCode += incCode + "|动感易,";
					}
					
					if(incCode.equals("2380000024")){
						zxrwIncrementCode += incCode + "|手机玩家,";
					}
					
					if(incCode.equals("2300660001")){
						zxrwIncrementCode += incCode + "|冲浪助手,";
					}
					
					if(incCode.equals("2400000262")){
						zxrwIncrementCode += incCode + "|无线音乐特级会员,";
					}
					
					if(incCode.equals("2380000145")){
						zxrwIncrementCode += incCode + "|新闻早晚报,";
					}
					
					// 4G业务
					if(incCode.equals("2000003758")){
						zxrwIncrementCode += incCode + "|上网流量包50元,";
					}
					
					if(incCode.equals("2000003759")){
						zxrwIncrementCode += incCode + "|上网流量包70元,";
					}
					
					if(incCode.equals("2000003760")){
						zxrwIncrementCode += incCode + "|上网流量包100元,";
					}
					
					if(incCode.equals("2000003761")){
						zxrwIncrementCode += incCode + "|上网流量包130元,";
					}
					
					if(incCode.equals("2000003762")){
						zxrwIncrementCode += incCode + "|上网流量包180元,";
					}
					
					if(incCode.equals("2000003766")){
						zxrwIncrementCode += incCode + "|短信包10元,";
					}
					
					if(incCode.equals("2000003767")){
						zxrwIncrementCode += incCode + "|短信包20元,";
					}
					
					if(incCode.equals("2000003768")){
						zxrwIncrementCode += incCode + "|短信包30元,";
					}
					
					if(incCode.equals("2000003769")){
						zxrwIncrementCode += incCode + "|彩信包10元,";
					}
					
					if(incCode.equals("2000003770")){
						zxrwIncrementCode += incCode + "|彩信包20元,";
					}
					
					if(incCode.equals("2000003771")){
						zxrwIncrementCode += incCode + "|彩信包30元,";
					}
					
					if(incCode.equals("2000003784")){
						zxrwIncrementCode += incCode + "|4G流量加油包10元,";
					}
					
					if(incCode.equals("2000003797")){
						zxrwIncrementCode += incCode + "|4G流量加油包50元,";
					}
				}
			}
		}
		
		if(zxrwIncrementCode.lastIndexOf(",") != -1){
			zxrwIncrementCode = zxrwIncrementCode.substring(0, zxrwIncrementCode.length() - 1);
		}
		
		return zxrwIncrementCode;
	}
	
	protected boolean checkStr(String str, String typeStr){
		try {
			String tmpStr = new String(str.getBytes("gb2312"),"iso-8859-1");
			if(typeStr.equals("1") && tmpStr.length() > 10){
				return true;
			}
			
			if(typeStr.equals("2") && tmpStr.length() > 18){
				return true;
			}
			
			if(typeStr.equals("3") && tmpStr.length() > 45){
				return true;
			}
			
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		return false;
	}
	
	protected String getCountyId(String cityId){
		String countyId = "0";
		if(cityId.equals("11")){
			countyId = "1101";
		}
		
		if(cityId.equals("12")){
			countyId = "1210";
		}
		
		if(cityId.equals("13")){
			countyId = "1315";
		}
		
		if(cityId.equals("14")){
			countyId = "1419";
		}
		
		if(cityId.equals("15")){
			countyId = "1527";
		}
		
		if(cityId.equals("16")){
			countyId = "1633";
		}
		
		if(cityId.equals("17")){
			countyId = "1738";
		}
		
		if(cityId.equals("18")){
			countyId = "1842";
		}
		
		if(cityId.equals("19")){
			countyId = "1946";
		}
		
		if(cityId.equals("20")){
			countyId = "2050";
		}
		
		if(cityId.equals("21")){
			countyId = "2157";
		}
		
		if(cityId.equals("22")){
			countyId = "2264";
		}
		
		if(cityId.equals("23")){
			countyId = "2371";
		}
		
		return countyId;
	}

	@Override
	public void validateLogin(CDSContext cdsContext, CDSResult result) {

	}

	@Override
	public void validateParam(CDSContext cdsContext, CDSResult result) {

	}

	public IchkForNetByNameService getChkForNetByNameService() {
		return chkForNetByNameService;
	}

	public void setChkForNetByNameService(
			IchkForNetByNameService chkForNetByNameService) {
		this.chkForNetByNameService = chkForNetByNameService;
	}

	public IWorkOrderService getWorkOrderService() {
		return workOrderService;
	}

	public void setWorkOrderService(IWorkOrderService workOrderService) {
		this.workOrderService = workOrderService;
	}
}
