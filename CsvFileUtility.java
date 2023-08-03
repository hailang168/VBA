//* XXXXXXXXシステム
//**************************************************:
/*
 * モジュール名
 * 	CsvFileUtility.java
 * 
 * 変更履歴
 *　変更日		変更者		変更概要
 *	2023/08/01	gm.kan		新規作成
 *
 */
package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;

/*
 * CSVファイル操作クラス。<br>
 * <br>
 * 以下の機能をサポートする。
 * <ul>
 * <li>ファイル作成処理</li>
 * <li>ファイル書き込み処理</li>
 * <li>ファイル書き込み終了処理</li>
 * </ul>
 * @since  2023/08/01
 * @version 1.0
 * @author  gm.kan
 */
public class CsvFileUtility {

	/*
	 * BufferedWriterオブジェクト
	 */
	private BufferedWriter bufferedWriter;

	/*
	 * BufferedReaderオブジェクト
	 */
	private BufferedReader bufferedReader;

	/*
	 * ファイルパス
	 */
	private String filePath = null;

	/*
	 * ファイルパス（実ファイル名）
	 */
	private String reallyFilePath = null;

	/*
	 * 出力ファイル
	 */
	private File fileObj = null;

	/*
	 * 読み取り書き込み行数
	 */
	private int readLine = 0;

	/*
	 * 出力ファイル数
	 */
	private int outputFileCount = 1;

	/*
	 * 出力済みファイルパスリスト
	 */
	private ArrayList<String> outputFilePathList = null;

	/*
	 * 項目長フラグ（バイト）
	 */
	private static final String SEPARETE_BYTE = "BYTE";

	/*
	 * プロパティファイルアクセル
	 */
	private CsvProperties prop = null;

	/*
	 * 出力バイト数合計
	 */
	private long byteSizeAmt = 0;

	/*
	 * ファイル名を分割する際のパッティング用変数
	 */
	private DecimalFormat paddingFormat;

	/*
	 * サポートするcharsetの名前
	 */
	private String charsetName = "";

	/**
	 * コンストラクタ<br>
	 * <b>CSVファイル書き込み用のコンストラクタ。</b><br>
	 * <br>
	 * @param filePath ファイルパス
	 * @param addFlg　追記フラグ
	 * @param charsetName　サポートするcharset名前
	 */
	public CsvFileUtility(String filePath, boolean addFlg, String charsetName) {

		// プロパティファイルアクセサの取得
		this.prop = CsvProperties.getInstance();

		try {
			// BufferedWriterクラスのインスタンス生成
			this.bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(filePath), charsetName));
			this.charsetName = charsetName;
			this.filePath = filePath;

			// 出力済ファイルパスリストインスタンス化
			this.outputFilePathList = new ArrayList<String>();
			this.outputFilePathList.add(this.filePath);

			// パディングフォーマットをプロパティから取得する
			this.paddingFormat = new DecimalFormat(this.prop.getPaddingFormat());

		} catch (UnsupportedEncodingException e) {
			// エンコード指定エラー
			// 可変パラメータ設定「さサポートするcharset名前」
			String[] kahenParam = { "サポートするcharsetの名前", charsetName };
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// 取得元のファイルが存在しまん
			// 可変パラメータ設定「ファイルパス」
			String[] kahenParam = { filePath };
			e.printStackTrace();
		}
	}

	/**
	 * コンストラクタ<br>
	 * <b>CSVファイル書き込み用のコンストラクタ。</b><br>
	 * <br>
	 * @param filePath ファイルパス
	 * @param addFlg　追記フラグ 
	 */
	public CsvFileUtility(String filePath, boolean addFlg) {

		// プロパティファイルアクセサの取得
		this.prop = CsvProperties.getInstance();

		try {
			// BufferedWriterクラスのインスタンス生成
			this.bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(filePath), "windows-31j"));
			this.charsetName = "windows-31j";
			this.filePath = filePath;

			// 出力済ファイルパスリストインスタンス化
			this.outputFilePathList = new ArrayList<String>();
			this.outputFilePathList.add(this.filePath);

			// パディングフォーマットをプロパティから取得する
			this.paddingFormat = new DecimalFormat(this.prop.getPaddingFormat());

		} catch (FileNotFoundException e) {
			// 可変パラメータ設定「ファイルパス」
			String[] kahenParam = { filePath };
			// 取得元のファイルが存在しまん
			e.printStackTrace();
		} catch (IOException e) {
			// 入出力処理に失敗しました。
			e.printStackTrace();
		}
	}

	/**
	 * コンストラクタ<br>
	 * <b>CSVファイル書き込み用のコンストラクタ。</b><br>
	 * <br>
	 * @param file ファイルオブジェクト
	 */
	public CsvFileUtility(File file) {

		// プロパティファイルアクセサの取得
		this.prop = CsvProperties.getInstance();

		try {
			// BufferedReaderクラスのインスタンス生成
			this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "windows-31j"));
			this.filePath = file.getPath();

		} catch (FileNotFoundException e) {
			// 可変パラメータ設定「ファイルパス」
			String[] kahenParam = { file.getPath() };
			// 取得元のファイルが存在しまん
			e.printStackTrace();
		} catch (IOException e) {
			// 入出力処理に失敗しました。
			e.printStackTrace();
		}
	}

	/**
	 * コンストラクタ<br>
	 * <b>CSVファイル書き込み用のコンストラクタ。</b><br>
	 * <br>
	 * @param filePath ファイルパス
	 */
	public CsvFileUtility(String filePath) {

		// プロパティファイルアクセサの取得
		this.prop = CsvProperties.getInstance();

		try {
			// BufferedReaderクラスのインスタンス生成
			this.bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(filePath), "windows-31j"));
			this.filePath = filePath;

		} catch (FileNotFoundException e) {
			// 可変パラメータ設定「ファイルパス」
			String[] kahenParam = { filePath };
			// 取得元のファイルが存在しまん
			e.printStackTrace();
		} catch (IOException e) {
			// 入出力処理に失敗しました。
			e.printStackTrace();
		}
	}

	/**
	 * コンストラクタ<br>
	 * <b>CSVファイル書き込み用のコンストラクタ。</b><br>
	 * <br>
	 * @param filePath ファイルパス
	 * @param charsetName　サポートするcharset名前
	 */
	public CsvFileUtility(String filePath, String charsetName) {

		// プロパティファイルアクセサの取得
		this.prop = CsvProperties.getInstance();

		try {
			// BufferedReaderクラスのインスタンス生成
			this.bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(filePath), charsetName));
			this.filePath = filePath;
			this.charsetName = charsetName;

		} catch (UnsupportedEncodingException e) {
			// エンコード指定エラー
			// 可変パラメータ設定「さサポートするcharset名前」
			String[] kahenParam = { charsetName };
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// 可変パラメータ設定「ファイルパス」
			String[] kahenParam = { filePath };
			// 取得元のファイルが存在しまん
			e.printStackTrace();
		} catch (IOException e) {
			// 入出力処理に失敗しました。
			e.printStackTrace();
		}
	}

	/**
	 * データ書き込み処理<br>
	 * <br>
	 * 使用例
	 * <pre>
	 * 	<code>instance.writeFileLine(writeDataList);</code>
	 * </pre>
	 * @param data データ
	 * @exception IOException システム重度エラー
	 */
	public void writeFileLine(String data) {
		try {
			// ファイルサイズ・行数をチェックし、ファイルサイズ・行数が多い場合は、ファイルの分割を行う。
			if (this.readLine + 1 > this.prop.getMaxLine() ||
					(this.byteSizeAmt + data.getBytes().length) > this.prop.getMaxFileSize()) {
				// 前ファイルの出力
				this.writeClose();

				this.fileObj = new File(this.reallyFilePath);
				// 拡張子は".csv", ".txt",なしの三つパターンを想定する
				int extensionIndex = this.filePath.toUpperCase().lastIndexOf(".CSV");
				if (extensionIndex == -1) {
					extensionIndex = this.filePath.toUpperCase().lastIndexOf(".TXT");
				}
				String extension = null;
				if (extensionIndex > 0) {
					extension = this.filePath.substring(extensionIndex);
				}
				StringBuffer tmpPath = new StringBuffer();
				if (extension == null) {
					// 拡張子なしの場合
					tmpPath.append(this.filePath);
				} else {
					// 拡張子ありの場合
					tmpPath.append(this.filePath.substring(0, extensionIndex));
				}

				// 各メンバ変数の初期化
				this.readLine = 0;

				// ２ファイル目の場合、１ファイル目のファイル名をリネームする
				if (this.outputFileCount == 2) {
					this.fileObj = new File(this.filePath);

					StringBuffer tmpPathFirst = new StringBuffer(tmpPath.toString());
					tmpPathFirst.append("_" + this.paddingFormat.format(1));
					if (extension != null) {
						tmpPathFirst.append(extension);
					}
					String toPath = tmpPathFirst.toString();
					String fromPath = this.filePath;
					// BatchUtility.copyFile(fromPath, toPath);
					Files.copy(Paths.get(fromPath), Paths.get(toPath));

					// 出力済みファイルリストも同様にリネーム
					this.outputFilePathList.clear();
					this.outputFilePathList.add(toPath);

				}
				// パディングする桁数をファイル数が候える場合はエラー
				if (Integer.toString(this.outputFileCount).length() > this.prop.getPaddingFormat().length()) {
					// エラーメッセージ出力
					String kahenParam[] = { String.valueOf(this.outputFileCount) };
					// todo ログ出力
				}

				// ファイル名の末尾に「_X」の数字を付番する
				tmpPath.append("_");
				tmpPath.append(this.paddingFormat.format(this.outputFileCount));
				// tmpPath.append(".csv");
				if (extension != null) {
					tmpPath.append(extension);
				}
				this.reallyFilePath = tmpPath.toString();
				this.fileObj = new File(this.reallyFilePath);
				// ファイル有無の確認
				if (this.fileObj.exists()) {
					// エラーメッセージ出力
					String kahenParam[] = { this.reallyFilePath };
					// todo log4
				}

				this.bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(this.reallyFilePath), this.charsetName));
				// 出力済みファイルリストにパスを追加
				this.outputFilePathList.add(tmpPath.toString());
				// 出力バイト数合計のクリア
				this.byteSizeAmt = 0;
			}

			// BufferedWriterオブジェクトがNULLである場合は書き込み処理を行わない
			if (this.bufferedWriter != null) {
				// データをファイルに書き込み
				this.bufferedWriter.write(Objects.toString(data, ""));
			}
			// 出力バイト数合計のカウントアップ
			this.byteSizeAmt += data.getBytes().length;
			//行カウンタのカウントアップ
			this.readLine++;

		} catch (IOException e) {
			// 入出力処理に失敗しました。
			e.printStackTrace();
		}
	}

	/**
	 * データ読み取り処理<br>
	 * <br>
	 * 使用例
	 * <pre>
	 * 	<code>String lineData = instance.readFileLine();</code>
	 * </pre>
	 * @return 一行分のデータ
	 * @exception IOException システム重度エラー
	 * @param data データ
	 */
	public String readFileLine() {
		// 一行分のデータ
		String lineData = null;

		try {
			// BufferedReaderオブジェクトがNULLである場合は読み込み処理を行わない
			if (this.bufferedReader != null) {
				// 一行読み込み
				lineData = this.bufferedReader.readLine();

				// 特殊文字がある場合は、文字が変換できず「&#[0-9]*;」となるため
				// 特殊文字を「?(&#9824;)」に変換する
				// EOFの場合は、異常終了しないように修正する
				if (lineData != null && lineData.contains("&#")) {
					lineData = lineData.replaceAll("&#[0-9]*;", "?");
				}
			}

		} catch (IOException e) {
			// 入出力処理に失敗しました。
			e.printStackTrace();
		}

		return lineData;
	}

	/**
	 * データ読み取り処理<br>
	 * <br>
	 * 使用例
	 * <pre>
	 * 	<code>String lineData = instance.readFileLineToArray();</code>
	 * </pre>
	 * @return 一行分のデータ
	 * @exception IOException システム重度エラー
	 */
	public ArrayList<String> readFileLineToArray() {
		// 1行ごとに格納する配列のインスタンス化
		ArrayList<String> lineDataList = new ArrayList<String>();

		try {
			String lineData = this.bufferedReader.readLine();
			if (lineData == null) {
				return null;
			}
			StringTokenizer st = new StringTokenizer(lineData, ",", true);
			boolean isEnd = true;
			boolean isComma = false;
			StringBuffer tmpMargeVal = null;
			// 最初のデータフラグ
			boolean isFirstData = true;
			while (true) {
				while (st.hasMoreElements()) {
					String tmpVal = st.nextToken();

					// 特殊文字がある場合は、文字が変換できず「&#[0-9]*;」となるため
					// 特殊文字を「?(&#9824;)」に変換する
					if (tmpVal.contains("&#")) {
						tmpVal = tmpVal.replaceAll("&#[0-9]*;", "?");
					}

					if (isEnd) {
						if (tmpVal.length() > 0 && tmpVal.charAt(0) == '\"') {
							if (tmpVal.length() > 1 && tmpVal.charAt(tmpVal.length() - 1) == '\"') {
								lineDataList.add(this.editInyoufu(tmpVal));
								isComma = false;
							}
							// 2文字目以降にダブルクォーテーションが来た場合は、１項目とと判定する
							else if (tmpVal.length() > 1 && 0 < tmpVal.indexOf('\"', 1)) {
								int endIndex = tmpVal.indexOf('\"', 1);
								tmpVal = editInyoufu(tmpVal, 1, endIndex);
								if (tmpVal != null) {
									tmpVal = tmpVal.trim();
								}
								lineDataList.add(tmpVal);
								isComma = false;
							} else {
								isEnd = false;
								tmpMargeVal = new StringBuffer(tmpVal);
								if (st.hasMoreElements()) {
									continue;
								}
								isComma = false;
							}
						} else {
							if (!",".equals(tmpVal)) {
								// 2文字目以降にダブルクォーテーションが来た場合は、１項目とと判定する
								if (tmpVal.length() > 1 && 0 < tmpVal.indexOf('\"', 1)) {
									int startIndex = tmpVal.indexOf('\"', 1);
									int endIndex = -1;
									if (startIndex > 0 && (startIndex + 1) < tmpVal.length()) {
										endIndex = tmpVal.indexOf('\"', startIndex + 1);
									}
									if (startIndex > 0 && endIndex > 0) {
										tmpVal = editInyoufu(tmpVal, startIndex + 1, endIndex);
									} else {
										isEnd = false;
										tmpMargeVal = new StringBuffer(tmpVal);
										if (st.hasMoreElements()) {
											continue;
										}
										isComma = false;
									}
								}
								if (tmpVal != null) {
									tmpVal = tmpVal.trim();
								}
								lineDataList.add(tmpVal);
								isComma = false;
							} else {
								if (isComma) {
									// カンマが２つ続いたら空文字を追加
									lineDataList.add("");
								} else if (isFirstData) {
									// 最初のデータが","の場合から文字を追加
									lineDataList.add("");
									isComma = true;
								} else {
									isComma = true;
								}
							}
						}
					} else {
						if (tmpVal.length() > 0 && tmpVal.charAt(tmpVal.length() - 1) == '\"') {
							tmpMargeVal.append(tmpVal);
							isEnd = true;
							lineDataList.add(this.editInyoufu(tmpMargeVal.toString()));
						} else {
							tmpMargeVal.append(tmpVal);
						}
					}
					if (isFirstData) {
						isFirstData = false;
					}
				}
				if (isEnd) {
					break;
				}
				lineData = this.bufferedReader.readLine();
				if (lineData == null) {
					lineDataList.add(this.editInyoufu(tmpMargeVal.toString()));
					break;
				}
				st = new StringTokenizer(lineData, ",", true);
				tmpMargeVal.append("\n");
			}
			// 最後がカンマで終わってる場合空文字を追加
			if (isComma) {
				lineDataList.add("");
			}
		} catch (IOException e) {
			// 入出力処理に失敗しました。
			e.printStackTrace();
		}

		return lineDataList;
	}

	/**
	 * データ読み取り処理。（固定長）<br>
	 * <br>
	 * 使用例
	 * <pre>
	 * 	<code>String lineData = instance.readFileLineToArray(itemLengthList);</code>
	 * </pre>
	 * @param	itemLengthList 項目長配列
	 * @return 一行分のデータ
	 * @exception IOException システム重度エラー
	 */
	public ArrayList<String> readFileLineToArray(ArrayList<Integer> itemLengthList) {

		// 1行ごとに格納する配列のインスタンス化
		ArrayList<String> lineDataList = new ArrayList<String>();
		try {

			String fixLineData = this.bufferedReader.readLine();
			if (fixLineData == null) {
				return null;
			}

			try {
				if (this.prop.getSeparateFlg().equals(SEPARETE_BYTE)) {

					byte[] bytes = null;
					if (this.charsetName == null || "".equals(this.charsetName)) {
						// 固定長文字列を取得
						bytes = fixLineData.getBytes("windows-31j");
					} else {
						// 固定長文字列を取得
						bytes = fixLineData.getBytes(this.charsetName);
					}
					// 1バイトずつループ
					int i = 0;
					while (i < bytes.length) {

						// 分割リスト分ループ
						for (int j = 0; j < itemLengthList.size(); j++) {
							// カラムごとのデータを格納するテンポラリ
							byte[] temp = null;
							int tempInt = 0;

							// 区切りbyteを超えた場合、文字列を取得
							temp = new byte[itemLengthList.get(j).intValue()];
							while (tempInt < itemLengthList.get(j).intValue()) {
								temp[tempInt] = bytes[i];
								tempInt++;
								i++;
							}

							// byte区切り位置を退避
							String tempResult = null;
							if (this.charsetName == null || "".equals(this.charsetName)) {
								tempResult = new String(temp, "windows-31j");
							} else {
								tempResult = new String(temp, this.charsetName);
							}
							// 特殊文字がある場合は、文字が変換できず「&#[0-9]*;」となるため
							// 特殊文字を「?(&#9824;)」に変換する
							if (tempResult.contains("&#")) {
								tempResult = tempResult.replaceAll("&#[0-9]*;", "?");
							}
							lineDataList.add(tempResult);
						}
					}
				} else {
					int startPoint = 0;
					int endPoint = 0;
					int endPoint2 = 0;
					// 分割リスト分ループ
					for (int j = 0; j < itemLengthList.size(); j++) {
						startPoint += endPoint2;
						endPoint2 = itemLengthList.get(j).intValue();
						endPoint = startPoint + endPoint2;
						String tempResult = fixLineData.substring(startPoint, endPoint);
						// 特殊文字がある場合は、文字が変換できず「&#[0-9]*;」となるため
						// 特殊文字を「?(&#9824;)」に変換する
						if (tempResult.contains("&#")) {
							tempResult = tempResult.replaceAll("&#[0-9]*;", "?");
						}
						lineDataList.add(tempResult);
					}
				}

			} catch (StringIndexOutOfBoundsException sie) {
				// 指定した区切り値に誤りがあります
				// CSVデータの読み込みに失敗しました。
				sie.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException aie) {
				// 指定した区切り値（バイト）に誤りがあります
				// CSVデータの読み込みに失敗しました。
				aie.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// CSVデータの読み込みに失敗しました。
				// 可変パラメータ設定
				String[] kahenParam = { "windows-31j" };
				e.printStackTrace();
			}

		} catch (IOException ioe) {
			// 入出力処理に失敗しました。
			ioe.printStackTrace();
		}
		return lineDataList;
	}

	/**
	 * 引用符編集処理。<br>
	 * <br>
	 * @param data データ
	 * @return 一行分のデータ
	 */
	private String editInyoufu(String val) {
		String returnVal = "";
		if (val != null && val.length() > 1) {
			if (val.charAt(0) == '\"' && val.charAt(val.length() - 1) == '\"') {
				returnVal = val.substring(1, val.length() - 1);
			} else {
				returnVal = val;
			}
		}
		return returnVal;
	}

	/**
	 * 引用符編集処理。開始終了文字列指定。<br>
	 * <br>
	 * @param data データ
	 * @return 一行分のデータ
	 */
	private String editInyoufu(String val, int startIndex, int endIndex) {
		String returnVal = "";
		if (val != null && val.length() > startIndex && (val.length() - 1) >= endIndex) {
			returnVal = val.substring(startIndex, endIndex);
		}
		return returnVal;
	}

	/**
	 * データ読み込み処理。<br>
	 * <br>
	 * @return 全行分のデータ
	 */
	public ArrayList<ArrayList<String>> readFileLineAllToArray() {
		ArrayList<ArrayList<String>> allData = new ArrayList<ArrayList<String>>();
		ArrayList<String> rowData = null;
		while ((rowData = this.readFileLineToArray()) != null) {
			allData.add(rowData);
		}
		return allData;
	}

	/**
	 * データ読み込み処理。固定長<br>
	 * <br>
	 * @param itemLengthList 項目長配列
	 * @return 全行分のデータ
	 */
	public ArrayList<ArrayList<String>> readFileLineAllToArray(ArrayList<Integer> itemLengthList) {
		ArrayList<ArrayList<String>> allData = new ArrayList<ArrayList<String>>();
		ArrayList<String> rowData = null;
		while ((rowData = this.readFileLineToArray(itemLengthList)) != null) {
			allData.add(rowData);
		}
		return allData;
	}

	/**
	 * ファイル書き込み反映処理
	 */
	private void flush() {
		try {

			// BufferedWriterオブジェクトがNULLではない場合
			if (this.bufferedWriter != null) {
				// ファイルの書き込みを反映させる
				this.bufferedWriter.flush();
			}
		} catch (IOException e) {
			// 入出力処理に失敗しました。
			e.printStackTrace();
		}
	}

	/**
	 * ファイル書き込み終了処理
	 */
	public void writeClose() {
		try {
			// 出力ファイル数カウンタのカウントアップ
			this.outputFileCount++;

			// BufferedWriterオブジェクトがNULLではない場合
			if (this.bufferedWriter != null) {
				// ファイルの書き込みを終了させる
				this.flush();
				this.bufferedWriter.close();
			}
		} catch (IOException e) {
			// 入出力処理に失敗しました。
			e.printStackTrace();
		}
	}

	/**
	 * ファイル読み込み終了処理
	 */
	public void readClose() {
		try {
			// BufferedReaderオブジェクトがNULLではない場合
			if (this.bufferedReader != null) {
				// ファイルの読み込みを終了させる
				this.bufferedReader.close();
			}
		} catch (IOException e) {
			// 入出力処理に失敗しました。
			e.printStackTrace();
		}
	}

	/**
	 * 出力済みファイルリストを取得する。<br>
	 * @return outputFilePathList
	 */
	public ArrayList<String> getOutputFilePathList() {
		return outputFilePathList;
	}

	/**
	 * 出力済みファイルリストを設定する。<br>
	 * @param outputFilePathList セットする outputFilePathList
	 */
	public void setOutputFilePathList(ArrayList<String> outputFilePathList) {
		this.outputFilePathList = outputFilePathList;
	}

	/**
	 * CSVファイルの「"」の数をチェックする。<br>
	 * ＊返却するエラーメッセージは、１行ごとにリストに格納され、１行ごとのリストには以下の順番に設定する。
	 * １番目：エラーメッセージ本文
	 * ２番目：対処方法
	 * ３番目：行番号
	 */
	public ArrayList<ArrayList<String>> checkCsvDoubleQuotation() {
		ArrayList<ArrayList<String>> errorMessageList = null;

		return errorMessageList;
	}

	/**
	 * 読み込みファイルが読み込み可能な最大サイズより小さいか判定する。<br>
	 * コンストラクタ実行後に呼び出すこと。
	 * 
	 * @return true:読み可能なファイル／false:読み不可能なファイル
	 */
	public boolean isCorrectFileSize() {

		boolean isCorrect = false;

		long maxSize = this.prop.getMaxFileSize();

		if (null != this.filePath && !"".equals(this.filePath)) {
			File file = new File(this.filePath);
			if (maxSize > file.length()) {
				isCorrect = true;
			}
		}

		return isCorrect;
	}

	/**
	 * 読み込みファイルが読み込み可能な最大行数より小さいか判定する。<br>
	 * コンストラクタ実行後に呼び出すこと。
	 * 
	 * @return true:読み可能なファイル／false:読み不可能なファイル
	 */
	public boolean isCorrectLineCount() {

		boolean isCorrect = false;

		long maxLineCount = this.prop.getMaxLine();

		if (null != this.filePath && !"".equals(this.filePath)) {
			ArrayList<ArrayList<String>> resultTmp = this.readFileLineAllToArray();
			int lineCount = resultTmp.size();

			if (maxLineCount >= lineCount) {
				isCorrect = true;
			}
		}

		return isCorrect;
	}

	/**
	 * 引数の文字列をファイルに出力する際にファイルを分割するかチェックする。<br>
	 * <br>
	 * @param data 出力行
	 * @return true:対象行を新規ファイルに出力する／false:対象行を現在書き込み中のファイルに出力する
	 */
	public boolean isWriteNextFile(String data) {

		boolean isChangeNextFile = false;

		if (this.readLine + 1 > this.prop.getMaxLine()
				|| (this.byteSizeAmt + data.getBytes().length) > this.prop.getMaxFileSize()) {
			isChangeNextFile = true;
		}

		return isChangeNextFile;
	}
}
