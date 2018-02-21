package com.atg.openssp.common.demand;

import java.util.List;

import com.atg.openssp.common.cache.dto.VideoAd;
import com.atg.openssp.common.cache.dto.Zone;

import openrtb.bidrequest.model.Publisher;
import openrtb.bidrequest.model.Site;

/**
 * Optimized for handling VideoAd impressions with the behaviour of very
 * individual requirements of the tag handler which binds to the SSP.
 * 
 * Use this class as data holder for the request params. Change the fields as
 * you require.
 * 
 * @author André Schmer
 *
 */
public class ParamValue {

	private Zone zone;
	private Site site;
	private VideoAd videoad;
	private String w;
	private String h;
	private List<String> mimes;
	private String domain;
	private String page;
	private List<Integer> protocols;
	private int startdelay;
	private String isTest;

	private Publisher publisher;

	public Site getSite() {
		return site;
	}

	public void setSite(final Site site) {
		this.site = site;
	}

	public String getW() {
		return w;
	}

	public void setW(final String w) {
		this.w = w;
	}

	public String getH() {
		return h;
	}

	public void setH(final String h) {
		this.h = h;
	}

	public List<String> getMimes() {
		return mimes;
	}

	public void setMimes(final List<String> mimes) {
		this.mimes = mimes;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(final String domain) {
		this.domain = domain;
	}

	public String getPage() {
		return page;
	}

	public void setPage(final String page) {
		this.page = page;
	}

	public void setProtocols(final List<Integer> list) {
		protocols = list;

	}

	public List<Integer> getProtocols() {
		return protocols;
	}

	public void setStartdelay(final int startdelay) {
		this.startdelay = startdelay;
	}

	public int getStartdelay() {
		return startdelay;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(final Publisher publisher) {
		this.publisher = publisher;
	}

	public String getIsTest() {
		return isTest;
	}

	public void setIsTest(final String isTest) {
		this.isTest = isTest;
	}
	
	

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public VideoAd getVideoad() {
		return videoad;
	}

	public void setVideoad(VideoAd videoad) {
		this.videoad = videoad;
	}

	@Override
	public String toString() {
		return String.format("ParamValue [site=%s, publisher=%s]", site, publisher);
	}

}
