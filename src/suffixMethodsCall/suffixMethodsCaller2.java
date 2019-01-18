package suffixMethodsCall;

public class suffixMethodsCaller2 {

	/**
	 * <pre>
	 * suffixMethodsに含まれる、suffix付きのgetterを全て実行し、
	 * 冒頭のmatchTargetCodeが含まれるか検証する.
	 * </pre>
	 */
	public static void main(String[] args) {

		// 計測スタート
//		long measureStart = System.currentTimeMillis();
		long measureStart = System.nanoTime();

		// マッチング対象の文字列
		String matchTargetCode = "J";

		suffixMethods sm = new suffixMethods();

		// マッチング対象の文字列が、suffix付きメソッドの実行結果リストに含まれるのかboolで出力
		System.out.println(sm.getReturnCode01().equals(matchTargetCode) || sm.getReturnCode02().equals(matchTargetCode)
				|| sm.getReturnCode03().equals(matchTargetCode) || sm.getReturnCode04().equals(matchTargetCode)
				|| sm.getReturnCode05().equals(matchTargetCode) || sm.getReturnCode06().equals(matchTargetCode)
				|| sm.getReturnCode07().equals(matchTargetCode) || sm.getReturnCode08().equals(matchTargetCode)
				|| sm.getReturnCode09().equals(matchTargetCode) || sm.getReturnCode10().equals(matchTargetCode));

		// 計測終わり！
//		long measureEnd = System.currentTimeMillis();
		long measureEnd = System.nanoTime();
//		System.out.println((measureEnd - measureStart) + "ms");
		System.out.println((measureEnd - measureStart) + "ns");
	}
}
