package screener;

/**
 * 
 * @author Renier Veiga
 * 
 *         Date: Sep 4, 2018
 * 
 *         The purpose of this class is to screen all assets on a given exchange
 *         and determine which assets have favorable entry points.
 * 
 *         For BTC we will only evaluate the trend for the BTC/USDT pair. For
 *         all the other alts we will evaluate both the alt/BTC and the alt/USDT
 *         pair.
 * 
 *         At this time my idea is to determine the trend for each valid BTC and
 *         USDT pair on a daily chart. Those with a favorable trend will be
 *         evaluated further on a 4 hour chart, then hourly. The assets that
 *         have favorable trend at this point will then be further analyzed vs a
 *         variety of chosen strategies.
 * 
 * 
 */
public class AssetScreener {

}
