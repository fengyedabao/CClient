/*
 * File Name:       CM.h
 *
 * Reference:
 *
 * Author:          leon jun
 *
 * Description:
 *      Connect Manager (CM) interface file.
 *
 * History:
 *      please record the history in the following format:
 *      verx.y.z    date        author      description or bug/cr number
 *      ----------------------------------------------------------------
 *      ver0.0.1    08/31/2015  leon	   first draft
 *          
 *  
 *CodeReview Log:
 *      please record the code review log in the following format:
 *      verx.y.z    date        author      description
 *      ----------------------------------------------------------------
 * 
 */

#ifndef _CM_H_
#define _CM_H_

#include "p2ptypes.hh"
#include <stdlib.h>

#ifdef POS_DLL  // create or using dll library
#   ifdef CM_EXPORTS   // dll support
#       define CM_API  __declspec(dllexport)
#   else    // dll using
#       define CM_API  __declspec(dllimport)
#   endif
#else   // create or using static library
#   define CM_API
#endif

#define interface	class
#define PURE = 0
#define LOG_FILE 0

#define POS_UUID_LEN 16
#define POS_KEY_LEN 28
#define DEFAULT_KEY "Leon.Liao@Honeywell.com1.01"

#if LOG_FILE
	#define POS_LOG print_file
#else
	#ifdef WIN32
	#define POS_LOG printf
	#elif defined __ANDROID__
	#include <android/log.h>
	#define ANDROID_LOG_TAG "p2p"
	#define POS_LOG(...) __android_log_print(ANDROID_LOG_INFO, ANDROID_LOG_TAG, __VA_ARGS__)
	#else
	#define POS_LOG printf
	#endif
#endif

#ifdef POSIX
#include <unistd.h>
#endif

namespace cricket {

typedef enum Ecm_addr_type{
	CM_ADDR_TYPE_UUID = 0, //uuid
	CM_ADDR_TYPE_SOCK,     //ip,port
	CM_ADDR_TYPE_UNID      //unknown id
}ECM_ADDR_TYPE;

typedef enum Ecm_key_type{
	CM_KEY_TYPE_SELF = 0,
	CM_KEY_TYPE_SERVER,
	CM_KEY_TYPE_CLIENT,
	CM_KEY_TYPE_TURN
}ECM_KEY_TYPE;

typedef struct Tp2pAddr{
	int32 type;
	//uint32 uuid;
	uint8 uuid[POS_UUID_LEN];
	uint32 ip;
	uint16 port;
	FILLER2;
}Tp2pAddr;
typedef struct Tp2pKey{
	int32 type;
	uint8 uuid[POS_UUID_LEN];
	uint8 key[POS_KEY_LEN];
}Tp2pKey;

typedef enum Ecm_result_type{
	CM_RESULT_SERVER = 0, //server status result
	CM_RESULT_CONNECT,	  //destination client connect status
	CM_RESULT_NAT,		  //NAT treaverse result
	CM_RESULT_TOKEN		  //verify token from cloud
}ECM_RESULT_TYPE;

typedef enum Ecm_status_type{
	CM_STATUS_FALSE = 0, 
	CM_STATUS_TRUE		 
}ECM_STATUS_TYPE;

typedef struct{
	int32 type;
	int32 status;
}Tp2pResult;
CM_API void UUIDExpand(int8 * uuid_expand, int8 * uuid_compact);
CM_API void DectoHex(uint8 *dstBuf, uint8 *srcBuf, uint32 dstSize);
CM_API void POS_Sleep(int32 ms);

/*
*	Call back of cm, used for receiving data from network.
*/
interface ICMClientCallBack
{
public:

	virtual void HandleCmEvent(
		uint32 msgID,	//msg type
		uint32 wParam,	//msg length
		int32* pParam,	//msg buffer
		void *srcAddr,	//network source address of the msg
		void *user = NULL
		) PURE;

};

class NetCallBack : public ICMClientCallBack
{
public:
    NetCallBack(){};
	void HandleCmEvent(uint32 msgID, uint32 wParam, int32* pParam, void *srcAddr, void *user = 0);
};

/************************************************************************************************
 *                                                                                              *
 *                  CONNECT MANAGER SERVER (CMS) INTERFACE                                      *
 *                                                                                              *
 ************************************************************************************************/

/*
 *    Name: P2P_init
 *    Description:
 *        Initialize connect manager.
 *    Parameters:
 *        pCallBack : callback func pointer.
 *    Return:
 *        < 0 : failed, check the error number form ECM_ERROR_CODE.
 *        otherwise : successful.
 *    Remark:
 *
 */
CM_API uint8* P2P_init(ICMClientCallBack *pCallBack, void * user, int8 *configPath, int8 *serialNo);

/*
 *    Name: P2P_token
 *    Description:
 *        verify token from cloud.
 *    Parameters:
 *        pToken : token buffer.
 *		  tokenlen : token buffer size
 *    Return:
 *        < 0 : failed, check the error number form ECM_ERROR_CODE.
 *        otherwise : successful.
 *    Remark:
 *
 */
CM_API int32 P2P_token(int8 *pToken, int8 *deviceID, int32 devicelen);


/*
 *    Name: P2P_uninit
 *    Description:
 *        Uninitialize connect manager.
 *    Parameters:
 *        
 *    Return:
 *        All successful.
 *    Remark:
 *
 */
CM_API int32 P2P_uninit(void);


/*
 *    Name: P2P_connect
 *    Description:
 *        connect to other client.
 *    Parameters:
 *        dstAddr : the Address of destination client.
 *    Return:
 *        < 0 : failed, check the error number form ECM_ERROR_CODE.
 *        otherwise : successful, return the register id.
 *    Remark:
 *
 */
CM_API int32 P2P_connect(Tp2pAddr *dstAddr);


/*
 *    Name: P2P_disconnect
 *    Description:
 *        disconnect to other client.
 *    Parameters:
 *        uuid : the unique id of other client.
 *    Return:
 *        < 0 : failed, check the error number form ECM_ERROR_CODE.
 *        otherwise : successful.
 *    Remark:
 *
 */
CM_API int32 P2P_disconnect(Tp2pAddr *dstAddr);

/*
 *    Name: P2P_sendto
 *    Description:
 *        Send data to destination without success validate.
 *    Parameters:
 *        pBuf : data buffer.
 *        iLen : data length.
 *        dstAddr : destination address.
 *    Return:
 *        == iLen : send success
 *        otherwise : failed send.
 *    Remark:
 *
 */
CM_API int32 P2P_sendto(int8 *pBuf, int32 iLen, Tp2pAddr *dstAddr, uint8 crypto, uint16 msgID=1);

/*
 *    Name: P2P_sendto_ByRUDP
 *    Description:
 *        Send data to destination by rudp.
 *    Parameters:
 *        pBuf : data buffer.
 *        iLen : data length.
 *        dstAddr : destination address.
 *    Return:
 *        == iLen : send success
 *        otherwise : failed send.
 *    Remark:
 *
 */
CM_API int32 P2P_sendto_ByRUDP(int8 *pBuf, int32 iLen, Tp2pAddr *dstAddr, uint8 crypto, uint16 msgID=1);

/*
 *    Name: P2P_sendresp
 *    Description:
 *        Send response to destination client without success validate.
 *    Parameters:
 *		  msg  : stun message header
 *        pBuf : data buffer.
 *        iLen : data length.
 *        pDstAddr : destination address.
 *    Return:
 *        == iLen : send success
 *        otherwise : failed send.
 *    Remark:
 *
 */
CM_API int32 P2P_sendresp(int32 msgtype, int8 *pBuf, int32 ilen, Tp2pAddr *dstAddr);

/*
 *    Name: P2P_setKey
 *    Description:
 *        set key to p2p library.
 *    Parameters:
 *		  key  : self, server or other client key
 *    Return:
 *        0 : set success
 *        otherwise : failed set.
 *    Remark:
 *
 */
CM_API int32 P2P_setKey(Tp2pKey *key);

/*
 *    Name: P2P_setAuthor
 *    Description:
 *        set author to p2p library.
 *    Parameters:
 *		  author  : compute by key
 *    Return:
 *        0 : set success
 *        otherwise : failed set.
 *    Remark:
 *
 */
CM_API int32 P2P_setAuthor(uint8* author);

/*
 *    Name: P2P_sendStatistics
 *    Description:
 *        send statistics to cloud.
 *    Parameters:
 *		  allclient  : all register client count
 *		  onlineclient : all online client count
 *    Return:
 *        0 : send success
 *        otherwise : failed set.
 *    Remark:
 *
 */
CM_API int32 P2P_sendStatistics(int32 allclient, int32 onlineclient);

}//namespace cricket

#endif //_CM_H_
