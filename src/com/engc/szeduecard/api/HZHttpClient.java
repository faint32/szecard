/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.engc.szeduecard.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import android.util.Log;

/**
 * @create 2012-8-7 14:05:45
 * 
 * @author Sanlion Du
 * @since JDK1.7
 * @version
 */
public class HZHttpClient {

	private final static Logger logger = Logger.getLogger(HZHttpClient.class);
	private List<NameValuePair> nvps;

	public static boolean post(String url, Map<String, Object> m) {
		HttpClient client = new DefaultHttpClient();
		try {
			HttpPost post = new HttpPost(url);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (String key : m.keySet()) {
				if (m.get(key).getClass().isArray()) {
					nvps.add(new BasicNameValuePair(key,
							null == m.get(key) ? ""
									: ((Object[]) m.get(key))[0].toString()));
				} else {
					nvps.add(new BasicNameValuePair(key,
							null == m.get(key) ? "" : (String) m.get(key)));
				}

			}
			try {
				// HttpEntity entity=new UrlEncodedFormEntity(nvps,"utf-8");
				post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader br = null;
				br = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				String msg = null;
				msg = br.readLine();
				if (msg.equalsIgnoreCase("true")) {
					return true;
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return false;
	}

	public HZHttpClient() {
	}

	/**
	 * 增加请求参数键值对
	 * 
	 * @param K
	 *            参数key
	 * @param V
	 *            参数value
	 */
	public void addNvp(String K, String V) {
		if (null == nvps) {
			nvps = new ArrayList<NameValuePair>();
		}
		nvps.add(new BasicNameValuePair(K, V));
	}

	public String postMethod(String url, Map<String, Object> params) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			if (params.get(key).getClass().isArray()) {
				nvps.add(new BasicNameValuePair(key,
						null == params.get(key) ? "" : ((Object[]) params
								.get(key))[0].toString()));
			} else {
				nvps.add(new BasicNameValuePair(key,
						null == params.get(key) ? "" : (String) params.get(key)));
			}
		}
		// post.setEntity()
		// new UrlEncodedFormEntity()
		try {
			// HttpEntity entity=new UrlEncodedFormEntity(nvps,"utf-8");
			post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpResponse response;
		String result = null;
		try {

			// HttpConnectionParams.setConnectionTimeout(post.getParams(),
			// 8000);

			response = client.execute(post);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				HttpEntity entity = response.getEntity();

				result = EntityUtils.toString(entity);
				// JSONObject jsonObject = JSONObject.fromObject(result);
				return result;
			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
				post.abort();
				// String turl=response.getLastHeader("location").getValue();
				// HttpGet get=new HttpGet(turl);
				HttpEntity entity = response.getEntity();
				return result;

			}
			return null;
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
			Log.i("", e.getMessage());
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage());
			Log.i("", e.getMessage());
			return null;
		}
	}

	// /**
	// * 增加请求参数键值对
	// *
	// * @param K 参数key
	// * @param V 参数value
	// */
	// public T postMethodByUTF16(String url, Map<String, Object> params) {
	// HttpClient client = new DefaultHttpClient();
	// HttpPost post = new HttpPost(url);
	// List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	// for (String key : params.keySet()) {
	// if (params.get(key).getClass().isArray()) {
	// nvps.add(new BasicNameValuePair(key,
	// null == params.get(key) ? ""
	// : ((Object[]) params.get(key))[0].toString()));
	// } else {
	// nvps.add(new BasicNameValuePair(key,
	// null == params.get(key) ? "" : (String) params.get(key)));
	// }
	// }
	// // post.setEntity()
	// // new UrlEncodedFormEntity()
	// try {
	// //HttpEntity entity=new UrlEncodedFormEntity(nvps,"utf-8");
	// post.setEntity(new UrlEncodedFormEntity(nvps,"utf-16"));
	// } catch (UnsupportedEncodingException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	//
	// HttpResponse response;
	// try {
	// response = client.execute(post);
	// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	// HttpEntity entity = response.getEntity();
	//
	// return jsonString2Object(entity.getContent(), clazz);
	// } else if (response.getStatusLine().getStatusCode() ==
	// HttpStatus.SC_MOVED_TEMPORARILY) {
	// post.abort();
	// // String turl=response.getLastHeader("location").getValue();
	// // HttpGet get=new HttpGet(turl);
	// HttpEntity entity = response.getEntity();
	// return jsonString2Object(entity.getContent(), clazz);
	//
	// }
	// return null;
	// } catch (ClientProtocolException e) {
	// logger.error(e.getMessage());
	// return null;
	// } catch (IOException e) {
	//
	// logger.error(e.getMessage());
	// System.out.println(e.getMessage());
	// return null;
	// }
	// }

	public boolean postMethod(String url) {
		HttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost(url);
		try {
			HttpEntity entity = new UrlEncodedFormEntity(nvps, "utf-8");
			// UrlEncodedFormEntity encoder=new UrlEncodedFormEntity(nvps);
			// encoder.setContentEncoding("utf-8");
			post.setEntity(entity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpResponse response;
		try {
			response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader br = null;
				String msg = null;
				br = new BufferedReader(new InputStreamReader(response
						.getEntity().getContent()));
				msg = br.readLine();
				if (msg.equalsIgnoreCase("true")) {
					return true;
				}
			}
			return false;
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
			return false;
		} catch (IllegalStateException e) {
			logger.error(e.getMessage());
			return false;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	// @SuppressWarnings("unchecked")
	// private T jsonString2Object(InputStream in, Class<T> clazz) {
	// ObjectMapper om = new ObjectMapper();
	// try {
	// String json="";
	// return (T) om.readValue(in, clazz);
	// return om.readValue( json, clazz);
	// } catch (JsonParseException e) {
	// logger.error(e.getMessage());
	// return null;
	// } catch (JsonMappingException e) {
	// logger.error(e.getMessage());
	// return null;
	// } catch (JsonProcessingException e) {
	// logger.error(e.getMessage());
	// return null;
	// } catch (IOException e) {
	// logger.error(e.getMessage());
	// return null;
	// }
	// }

	// public static void sendData(Object obj, HttpServletResponse response)
	// throws IOException {
	// ObjectMapper om = new ObjectMapper();
	// PrintWriter pw = response.getWriter();
	// pw.print(om.writeValueAsString(obj));
	// pw.close();
	// }
}
