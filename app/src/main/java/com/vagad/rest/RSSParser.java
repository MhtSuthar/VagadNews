package com.vagad.rest;

import android.util.Log;

import com.vagad.model.RSSFeed;
import com.vagad.model.RSSItem;
import com.vagad.utils.Constants;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class RSSParser {

	// RSS XML document CHANNEL tag
	private static String TAG_CHANNEL = "channel";
	private static String TAG_TITLE = "title";
	private static String TAG_LINK = "link";
	private static String TAG_DESRIPTION = "description";
	private static String TAG_LANGUAGE = "language";
	private static String TAG_ITEM = "item";
	private static String TAG_PUB_DATE = "pubDate";
	private static String TAG_GUID = "guid";
	private static String TAG_IMAGE = "image";

	// constructor
	public RSSParser() {

	}

	/***
	 * Get RSS feed from url
	 * 
	 * @param url - is url of the website 
	 * @return RSSFeed class object
	 */
	public RSSFeed getRSSFeed(String url) {
		RSSFeed rssFeed = null;
		String rss_feed_xml = null;
		
		// getting rss link from html source code
		String rss_url = this.getRSSLinkFromURL(url);
		
		// check if rss_link is found or not
		if (rss_url != null) {
			// RSS url found
			// get RSS XML from rss ulr
			rss_feed_xml = this.getXmlFromUrl(rss_url);
			// check if RSS XML fetched or not
			if (rss_feed_xml != null) {
				// successfully fetched rss xml
				// parse the xml
				try {
					Document doc = this.getDomElement(rss_feed_xml);
					NodeList nodeList = doc.getElementsByTagName(TAG_CHANNEL);
					Element e = (Element) nodeList.item(0);
					
					// RSS nodes
					String title = this.getValue(e, TAG_TITLE);
					String link = this.getValue(e, TAG_LINK);
					String description = this.getValue(e, TAG_DESRIPTION);
					String language = this.getValue(e, TAG_LANGUAGE);

					// Creating new RSS Feed
					rssFeed = new RSSFeed(title, description, link, rss_url, language);
				} catch (Exception e) {
					// Check log for errors
					e.printStackTrace();
				}

			} else {
				// failed to fetch rss xml
			}
		} else {
			// no RSS url found
		}
		return rssFeed;
	}

	/**
	 * Getting RSS feed items <item>
	 * 
	 * @param - rss link url of the website
	 * @return - List of RSSItem class objects
	 * */
	public List<RSSItem> getRSSFeedItems(String rss_url){
		List<RSSItem> itemsList = new ArrayList<RSSItem>();
		String rss_feed_xml;
		
		// get RSS XML from rss url
		rss_feed_xml = this.getXmlFromUrl(rss_url);
		
		// check if RSS XML fetched or not
		if(rss_feed_xml != null){
			// successfully fetched rss xml
			// parse the xml
			try{
				Document doc = this.getDomElement(rss_feed_xml);
				NodeList nodeList = doc.getElementsByTagName(TAG_CHANNEL);
				Element e = (Element) nodeList.item(0);
				
				// Getting items array
				NodeList items = e.getElementsByTagName(TAG_ITEM);
				
				// looping through each item
				for(int i = 0; i < items.getLength(); i++){
					Element e1 = (Element) items.item(i);
					
					String title = this.getValue(e1, TAG_TITLE);
					String link = this.getValue(e1, TAG_LINK);
					String description = this.getValue(e1, TAG_DESRIPTION);
					String pubdate = this.getValue(e1, TAG_PUB_DATE);
					String guid = this.getValue(e1, TAG_GUID);
					String image = this.getValue(e1, TAG_IMAGE);
					
					RSSItem rssItem = new RSSItem(title, link, description, pubdate, guid, image, getNewsType(rss_url));
					
					// adding item to list
					itemsList.add(rssItem);
				}
			}catch(Exception e){
				// Check log for errors
				e.printStackTrace();
			}
		}
		
		// return item list
		return itemsList;
	}

	private String getNewsType(String rss_url) {
		if(rss_url.contains(Constants.NEWS_TYPE_DUNGARPUR))
		  return Constants.NEWS_TYPE_DUNGARPUR;
		else if(rss_url.contains(Constants.NEWS_TYPE_BANSWARA))
			return Constants.NEWS_TYPE_DUNGARPUR;
		else if(rss_url.contains(Constants.NEWS_TYPE_UDAIPUR))
			return Constants.NEWS_TYPE_DUNGARPUR;
		else if(rss_url.contains(Constants.NEWS_TYPE_UDAIPUR))
			return Constants.NEWS_TYPE_LATEST;
		else
			return "";
	}

	/**
	 * Getting RSS feed link from HTML source code
	 * 
	 * @param ulr is url of the website
	 * @returns url of rss link of website
	 * */
	public String getRSSLinkFromURL(String url) {
		// RSS url
		String rss_url = null;

		try {
			// Using JSoup library to parse the html source code
			org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
			// finding rss links which are having link[type=application/rss+xml]
			org.jsoup.select.Elements links = doc
					.select("link[type=application/rss+xml]");
			
			Log.d("No of RSS links found", " " + links.size());
			
			// check if urls found or not
			if (links.size() > 0) {
				rss_url = links.get(0).attr("href").toString();
			} else {
				// finding rss links which are having link[type=application/rss+xml]
				org.jsoup.select.Elements links1 = doc
						.select("link[type=application/atom+xml]");
				if(links1.size() > 0){
					rss_url = links1.get(0).attr("href").toString();	
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		// returing RSS url
		return rss_url;
	}

	/**
	 * Method to get xml content from url HTTP Get request
	 * */
	public String getXmlFromUrl(String urlFeed) {
		URL url;
		StringBuffer response = null;
		HttpURLConnection urlConnection = null;
		try {
			Log.e("Url",""+urlFeed);
			url = new URL(urlFeed);

			urlConnection = (HttpURLConnection) url
					.openConnection();

			BufferedReader in = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			Log.e("response", "" + response);
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}

		/*String xml = null;
		try {
			// request method is GET
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		// return XML
		return response.toString();
	}

	/**
	 * Getting XML DOM element
	 * 
	 * @param XML string
	 * */
	public Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = (Document) db.parse(is);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}

		return doc;
	}

	/**
	 * Getting node value
	 * 
	 * @param elem element
	 */
	public final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE || ( child.getNodeType() == Node.CDATA_SECTION_NODE)) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	/**
	 * Getting node value
	 * 
	 * @param Element node
	 * @param key  string
	 * */
	public String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return this.getElementValue(n.item(0));
	}
}
