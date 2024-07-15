package azule;

import java.io.*;
import java.awt.*;
import java.lang.*;
import java.util.*;

import javax.swing.*;

public class AzuleCompiler {
	
	static ArrayList<String> objNameArray = new ArrayList<String>();
	static ArrayList<String> objTypeArray = new ArrayList<String>();
	static ArrayList<Object> objValueArray = new ArrayList<Object>();
	
	static ArrayList<String> funcNameArray = new ArrayList<String>();
	static ArrayList<ArrayList<String>> funcLinesArray = new ArrayList<ArrayList<String>>();
	static ArrayList<Integer> funcParamArray = new ArrayList<Integer>();
	
	static ArrayList<Integer> forLoopRepeatLength = new ArrayList<Integer>();
	static ArrayList<Integer> forLoopObjIndex = new ArrayList<Integer>();
	
	@SuppressWarnings("resource")
	public static int compile(String code, JTextArea console, int lineNumber, Boolean fromFirstCompile) {
		//reset all of the previous info
		if(fromFirstCompile) {
			console.setText("");
			objNameArray = new ArrayList<String>();
			objTypeArray = new ArrayList<String>();
			objValueArray = new ArrayList<Object>();
			funcNameArray = new ArrayList<String>();
			funcLinesArray = new ArrayList<ArrayList<String>>();
			funcParamArray = new ArrayList<Integer>();
			forLoopRepeatLength = new ArrayList<Integer>();
			forLoopObjIndex = new ArrayList<Integer>();
		}
		//code = code.replaceAll(" ", "");
		//code = code.replaceAll("\\t", "");
		Scanner input = new Scanner(code);
		String line;
		String[] lineParts;
		int lineFromCalled = 0;
		while(input.hasNextLine()) {
			lineFromCalled++;
			line=input.nextLine();
			lineParts = line.split("\\.");
			int returnCode = readLine(lineParts, lineNumber, console, code);
			if(returnCode==-1)
				return -1;
			else if(returnCode==1)
			    while(!input.nextLine().equals("}")) {lineNumber++;}
			else if(returnCode>=2) {
				int startNumber = lineNumber;
				for(int i = 0; i < forLoopRepeatLength.get(returnCode-2); i++) {
					Scanner forInput = new Scanner(code);
					for(int j = 0; j < lineFromCalled; j++)
							forInput.nextLine();
					lineNumber = startNumber;
					String forCode = "";
					//the int fors will count how many for loops it has to pass before finding this for loops terminator
					int fors = 1;
					while(fors!=0) {
						if((line = forInput.nextLine()).split("\\.")[0].equals("for"))
							fors++;
						if(line.equals(";"))
							fors--;
						if(fors>0) {
							lineNumber++;
							forCode = forCode + line + "\n";
						}
					}
					if(compile(forCode, console, startNumber+1, false) == -1)
						return -1;
					forInput.close();
					objValueArray.set(forLoopObjIndex.get(returnCode-2), i+1);
				}
				objNameArray.remove(forLoopObjIndex.get(returnCode-2).intValue());
				objValueArray.remove(forLoopObjIndex.get(returnCode-2).intValue());
				objTypeArray.remove(forLoopObjIndex.get(returnCode-2).intValue());
				while(!input.nextLine().equals(";")) {}
			} 
			lineNumber++;
		}
		input.close();
		return 0;
	}
	/**
	 * 
	 * Each returned value has a different meaning:
	 * -1 = Exception thrown and process should be killed;
	 * 0 = Line run successfully;
	 * 1 = Function detected and skip lines until the end of the function
	 * >1 = For loop detected and repeat lines x times
	 * 
	 */
	public static int readLine(String[] lineParts, int lineNumber, JTextArea console, String code) {
		String firstPart = lineParts[0];
		if(firstPart.equals("new")) {
			try {
				if(!lineParts[2].equals("")) {
					if(getObjIndex(lineParts[2]) == -1) {
						switch(lineParts[1]) {
							case "int":
								try {
									objValueArray.add(Integer.parseInt(lineParts[3]));
								} catch(NumberFormatException arg0) { 
									errorMessage(AzuleExceptions.IncorrectDataTypeException(lineParts[3], "int"), lineNumber, console); 
									return -1;
								} catch(IndexOutOfBoundsException arg0) {
									objValueArray.add(null);
								}
								objTypeArray.add("Integer");
								break;
							case "str":
								objValueArray.add(lineParts[3]);
								objTypeArray.add("String");
								break;
							case "bool":
								if(getBooleanValue(lineParts[3]) == 1)
									objValueArray.add(true);
								else if(getBooleanValue(lineParts[3]) == 0)
									objValueArray.add(false);
								else if(getBooleanValue(lineParts[3]) == -1) {
									errorMessage(AzuleExceptions.IncorrectDataTypeException(lineParts[3], "bool"), lineNumber, console);
									return -1;
								}
								objTypeArray.add("Boolean");
								break;
							case "int(math)":
								Integer value = math(lineParts[3], lineParts[4], lineParts[5], lineNumber, console);
								if(value==null)
									return -1;
								else
									objValueArray.add((int)value);
								objTypeArray.add("Integer");
                                break;
							default:
								errorMessage(AzuleExceptions.InvalidObjectTypeException(lineParts[1]), lineNumber, console);
								return -1;
						}
						objNameArray.add(lineParts[2]);
					} else {
						errorMessage(AzuleExceptions.ObjectAlreadyExistsException(lineParts[2]), lineNumber, console);
						return -1;
					}
				} else {
					errorMessage(AzuleExceptions.NullNameException(), lineNumber, console);
					return -1;
				}
			} catch(IndexOutOfBoundsException arg0) {
				errorMessage(AzuleExceptions.NullNameException(), lineNumber, console);
				return -1;
			}
		} else if(firstPart.equals("func")) {
			if(!lineParts[1].equals("")) {
				if(getObjIndex(lineParts[1]) == -1) {
		            ArrayList<String> functionLines = new ArrayList<String>();
		            String[] functionParameters = {};
					try {
						functionParameters = lineParts[1].split("[()]")[1].split(",");
						funcParamArray.add(functionParameters.length);
						for(String s : functionParameters)
							functionLines.add("new." + s.split("::")[0] + "." + s.split("::")[1] + ".");
					} catch(IndexOutOfBoundsException arg0) {}
		            lineParts[1] = lineParts[1].split("[()]")[0];
		            Scanner sc = new Scanner(code);
		            String line;
		            for(int i=0; i<lineNumber; i++)
		                sc.nextLine();
		            while(!(line = sc.nextLine()).equals("}"))
		                functionLines.add(line);
		            sc.close();
		            for(String s : functionParameters)
		            	functionLines.add("del." + s.split("::")[1]);
		            funcLinesArray.add(functionLines);
		            funcNameArray.add(lineParts[1]);
		            return 1;
				} else {
					errorMessage(AzuleExceptions.ObjectAlreadyExistsException(lineParts[2]), lineNumber, console);
					return -1;
				}
			} else {
				errorMessage(AzuleExceptions.NullNameException(), lineNumber, console);
				return -1;
			}
		} else if(firstPart.equals("write")) {
			Object printValue = null;
			if(lineParts[1].contains("\""))
				printValue = lineParts[1].split("\"")[1];
			else
				try {
					printValue = getObjValue(lineParts[1]);
				} catch(IndexOutOfBoundsException arg0) {
					errorMessage(AzuleExceptions.NonExistantObjectException(lineParts[1]), lineNumber, console);
					return -1;
				}
			if(printValue==null) {
				errorMessage(AzuleExceptions.NullPointerException(lineParts[1]), lineNumber, console);
				return -1;
			} else
				addConsoleText(printValue, console);
		} else if(firstPart.equals("set")) {
			String objClass = "";
			try {
				objClass = getObjType(lineParts[1]);
			} catch(IndexOutOfBoundsException arg0) {
				errorMessage(AzuleExceptions.NonExistantObjectException(lineParts[1]), lineNumber, console);
				return -1;
			}
			if(lineParts[2].equals("data")) {
				switch(objClass) {
					case "Integer":
						try { 
							objValueArray.set(getObjIndex(lineParts[1]), Integer.parseInt(lineParts[3])); 
						} catch(NumberFormatException arg0) { 
							errorMessage(AzuleExceptions.IncorrectDataTypeException(lineParts[3], "int"), lineNumber, console); 
							return -1;
						}
						break;
					case "String":
						objValueArray.set(getObjIndex(lineParts[1]), lineParts[3]);
						break;
					case "Boolean":
						try { 
							int index = getObjIndex(lineParts[1]);
							if(getBooleanValue(lineParts[3]) == 1)
								objValueArray.set(index, true);
							else if(getBooleanValue(lineParts[3]) == 0)
								objValueArray.set(index, false);
							else if(getBooleanValue(lineParts[3]) == -1) {
								errorMessage(AzuleExceptions.IncorrectDataTypeException(lineParts[3], "bool"), lineNumber, console);
								return -1;
							}
						} catch(NumberFormatException arg0) { 
							errorMessage(AzuleExceptions.IncorrectDataTypeException(lineParts[3], "bool"), lineNumber, console); 
							return -1;
						}
						break;
				}
			} else if(lineParts[2].equals("obj")) {
				try {
					String objClass2 = getObjType(lineParts[3]);
					if(objClass.equals(objClass2))
						objValueArray.set(getObjIndex(lineParts[1]), getObjValue(lineParts[3]));
					else {
						switch(objClass) {
							case "Integer":
								objClass = "int";
								break;
							case "String":
								objClass = "str";
								break;
							case "Boolean":
								objClass = "bool";
								break;
						}
						errorMessage(AzuleExceptions.IncorrectDataTypeExceptionFromAnObject(lineParts[3], objClass), lineNumber, console);
					}
				} catch(IndexOutOfBoundsException arg0) {
					errorMessage(AzuleExceptions.NonExistantObjectException(lineParts[3]), lineNumber, console);
					return -1;
				}
			} else if(lineParts[2].equals("math")) {
			    try {
			        objValueArray.set(getObjIndex(lineParts[1]), math(lineParts[3], lineParts[4], lineParts[5], lineNumber, console));
			    } catch (NullPointerException arg0) {
			        return -1;
			    }
			}
		} else if(firstPart.equals("for")) {
			if(lineParts[1].equals("")) {
				errorMessage(AzuleExceptions.NullNameException(), lineNumber, console);
				return -1;
			} else if(getObjIndex(lineParts[1]) == -1){
				try {
					forLoopObjIndex.add(objNameArray.size());
					if(lineParts[2].split("[()]")[0].equals("obj"))
						try {
							forLoopRepeatLength.add((int)getObjValue(lineParts[2].split("[()]")[1]));
						} catch(IndexOutOfBoundsException arg0) {
							errorMessage(AzuleExceptions.NonExistantObjectException(lineParts[2].split("[()]")[1]), lineNumber, console);
							return -1;
						} catch(ClassCastException arg0) {
							errorMessage(AzuleExceptions.IncorrectDataTypeExceptionFromAnObject(lineParts[2].split("[()]")[1], "int"), lineNumber, console);
							return -1;
						}
					else
						forLoopRepeatLength.add(Integer.parseInt(lineParts[2]));
					objNameArray.add(lineParts[1]);
					objValueArray.add(0);
					objTypeArray.add("Integer");
					return forLoopObjIndex.size()+1;
				} catch(NumberFormatException arg0) {
					errorMessage(AzuleExceptions.IncorrectDataTypeException(lineParts[2], "int"), lineNumber, console);
					return -1;
				}
			} else {
				errorMessage(AzuleExceptions.ObjectAlreadyExistsException(lineParts[1]), lineNumber, console);
				return -1;
			}
		} else if(firstPart.equals("call")) {
		    int indexOfFunc = 0;
			try {
		    	indexOfFunc = funcNameArray.indexOf(lineParts[1].split("[()]")[0]);
		    } catch(IndexOutOfBoundsException arg0) {
		    	errorMessage(AzuleExceptions.NonExistantObjectException(lineParts[1]), lineNumber, console);
		    	return -1;
		    }
			ArrayList<String> lines = funcLinesArray.get(indexOfFunc);
			Boolean noParams = false;
			String[] params = null;
			try{params = lineParts[1].split("[()]")[1].split(",");} catch(IndexOutOfBoundsException arg0) {noParams = true;}
			if(!noParams)
				for(int i = 0; i < funcParamArray.get(indexOfFunc); i++)
					lines.set(i, lines.get(i) + params[i]);
			String functionCode = "";
		    for(int i = 0; i < lines.size(); i++)
		    	functionCode = functionCode + lines.get(i) + "\n";
		    	//readLine(lines.get(i).split("\\."), lineNumber, console, code);
		    if(compile(functionCode, console, lineNumber, false) == -1)
		    	return -1;
		    if(!noParams)
				for(int i = 0; i < funcParamArray.get(indexOfFunc); i++) {
					lines.set(i, lines.get(i).split("\\.")[0] + "." + lines.get(i).split("\\.")[1] + "." + lines.get(i).split("\\.")[2] + ".");
				}
		} else if(firstPart.equals("del")) {
			try {
				int index = getObjIndex(lineParts[1]);
				objNameArray.remove(index);
				objValueArray.remove(index);
				objTypeArray.remove(index);
			} catch(IndexOutOfBoundsException arg0) {
				errorMessage(AzuleExceptions.NonExistantObjectException(lineParts[1]), lineNumber, console);
				return -1;
			}
		} else if(firstPart.equals(";")) {
		} else {
			errorMessage(AzuleExceptions.UnknownInitialFunctionException(lineParts[0]), lineNumber, console);
			return -1;
		}
		return 0;
	}
	
	
	public static void addConsoleText(Object textToAdd, JTextArea console) { console.setText(console.getText() + textToAdd + "\n"); }
	
	public static void errorMessage(String errorMessage, int lineNumber, JTextArea console) { console.setText(console.getText() + errorMessage + "\n ERROR AT LINE: " + lineNumber); }
	
	public static int getObjIndex(String name) { return objNameArray.indexOf(name); }
	
	public static Object getObjValue(String name) { return objValueArray.get(objNameArray.indexOf(name)); }
	
	public static String getObjType(String name) { return objTypeArray.get(getObjIndex(name)); }
	
	public static int getBooleanValue(String input) { 
		if(input.toLowerCase().equals("true")) {
			return 1;
		} else if(input.toLowerCase().equals("false")) {
			return 0;
		} else {
			return -1;
		}
	}
	
	public static Integer math(String operand, String firstValue, String secondValue, int lineNumber, JTextArea console) {
		int v1 = 0;
		int v2 = 0;
		try {
			if(firstValue.contains("obj")) {
				 firstValue = firstValue.split("[()]")[1];
				 v1 = (int)getObjValue(firstValue);
			} else
			    v1 = Integer.parseInt(firstValue);
		} catch(NumberFormatException | ClassCastException arg0) {
	        errorMessage(AzuleExceptions.IncorrectDataTypeException(firstValue, "int"), lineNumber, console); 
	        return null;
		} catch(NullPointerException arg0) {
			errorMessage(AzuleExceptions.NullPointerException(firstValue), lineNumber, console);
			return null;
		} catch(IndexOutOfBoundsException arg0) {
			errorMessage(AzuleExceptions.NonExistantObjectException(firstValue), lineNumber, console);
			return null;
		}
		try {
			if(secondValue.contains("obj")) {
				secondValue = secondValue.split("[()]")[1];
				v2 = (int)getObjValue(secondValue);
			} else
				v2 = Integer.parseInt(secondValue);
		} catch(NumberFormatException | ClassCastException arg0) {
	        errorMessage(AzuleExceptions.IncorrectDataTypeException(secondValue, "int"), lineNumber, console); 
	        return null;
		} catch(NullPointerException arg0) {
			errorMessage(AzuleExceptions.NullPointerException(secondValue), lineNumber, console);
			return null;
		} catch(IndexOutOfBoundsException arg0) {
			errorMessage(AzuleExceptions.NonExistantObjectException(secondValue), lineNumber, console);
			return null;
		}
		switch (operand) {
			case "add":
				return v1+v2;
			case "sub":
				return v1-v2;
			case "mult":
				return v1*v2;
			case "div":
				return v1/v2;
			default:
			    errorMessage(AzuleExceptions.InvalidParameterException(operand, "math"), lineNumber, console);
				return null;
		}
	}
	
}