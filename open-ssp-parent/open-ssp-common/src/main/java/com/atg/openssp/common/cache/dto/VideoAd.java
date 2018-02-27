package com.atg.openssp.common.cache.dto;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * @author André Schmer, TrieuNT
 *
 */
public class VideoAd implements Serializable {

	private static final long serialVersionUID = 2035631518654057068L;

	@SerializedName("videoad_id")
	private int videoadId = 0;

	@SerializedName("bidfloor_currency")
	private String bidfloorCurrency = "EUR";

	@SerializedName("bidfloor_price")
	private float bidfloorPrice = 0F;

	@SerializedName("min_duration")
	private int minDuration = 0;

	@SerializedName("max_duration")
	private int maxDuration = 0;

	public VideoAd() {}
	
	

	public VideoAd(int videoadId, String bidfloorCurrency, float bidfloorPrice, int minDuration, int maxDuration) {
		super();
		this.videoadId = videoadId;
		this.bidfloorCurrency = bidfloorCurrency;
		this.bidfloorPrice = bidfloorPrice;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
	}


	public int getVideoadId() {
		return videoadId;
	}

	public void setVideoadId(final int videoadId) {
		this.videoadId = videoadId;
	}

	public String getBidfloorCurrency() {
		return bidfloorCurrency;
	}

	public void setBidfloorCurrency(final String bidfloorCurrency) {
		this.bidfloorCurrency = bidfloorCurrency;
	}

	public float getBidfloorPrice() {
		return bidfloorPrice;
	}

	public void setBidfloorPrice(final float bidfloorPrice) {
		this.bidfloorPrice = bidfloorPrice;
	}

	public int getMinDuration() {
		return minDuration;
	}

	public void setMinDuration(final int minDuration) {
		this.minDuration = minDuration;
	}

	public int getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(final int maxDuration) {
		this.maxDuration = maxDuration;
	}

	@Override
	public String toString() {
		return String.format("VideoAd [videoadId=%s, bidfloorCurrency=%s, bidfloorPrice=%s, minDuration=%s, maxDuration=%s]", videoadId, bidfloorCurrency, bidfloorPrice,
		        minDuration, maxDuration);
	}

}
