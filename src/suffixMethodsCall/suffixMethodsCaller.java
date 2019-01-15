package suffixMethodsCall;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class suffixMethodsCaller {

	/**
	 * <pre>
	 * suffixMethodsに含まれる、suffix付きのgetterを全て実行し、
	 * 冒頭のmatchTargetCodeが含まれるか検証する.
	 * </pre>
	 */
	public static void main(String[] args) {

		// マッチング対象の文字列
		String matchTargetCode = "J";
		// suffix付きのメソッドから取得できたコードリスト
		List<String> codeList = new ArrayList<String>();

        // クラス、メソッドを文字列で指定
		suffixMethods sm = new suffixMethods();
        String clazz = sm.getClass().getName();
        String baseSuffixMethod = "getReturnCode";
        int suffixPartStart = 1;
        int suffixPartEnd = 10;

        // リフレクションを利用して、suffix付きのメソッドを実行
        try {
            Class<?> c = Class.forName(clazz);
            Object myObj = c.newInstance();

            // suffix部分をループさせてコードリストに突っ込む
            for ( ; suffixPartStart <= suffixPartEnd; suffixPartStart++) {
                // 実行メソッドの設定
                Method m = c.getMethod(baseSuffixMethod + String.format("%02d", suffixPartStart));
                // コードリストに詰め込む
                codeList.add(m.invoke(myObj).toString());
            }
        } catch(ReflectiveOperationException e) {
            e.printStackTrace();
        }

        // マッチング対象の文字列が、suffix付きメソッドの実行結果リストに含まれるのかboolで出力
        System.out.println(codeList.contains(matchTargetCode));
	}
}
