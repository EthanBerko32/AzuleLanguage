package azule;

public class AzuleExceptions {
	
	public static String UnknownInitialFunctionException(String object) {
		return "UnknownInitialFunctionException: The function \"" + object + "\" is unknown";
	}
	
	public static String NullPointerException(String object) {
		return "NullPointerException: Object \"" + object + "\" is null and cannot be used";
	}
	
	public static String NonExistantObjectException(String object) {
		return "NonExistantObjectException: The object \"" + object + "\" doesn't exist";
	}
	
	public static String ObjectAlreadyExistsException(String object) {
		return "ObjectAlreadyExistsException: The object \"" + object + "\" already exists";
	}
	
	public static String InvalidObjectTypeException(String object) {
		return "InvalidObjectTypeException: The object type \"" + object + "\" is invalid";
	}
	
	public static String IncorrectDataTypeException(String data, String type) {
		return "IncorrectDataTypeException: The given value \"" + data + "\" does not match the object data type " + type;
	}
	
	public static String IncorrectDataTypeExceptionFromAnObject(String data, String type) {
		return "IncorrectDataTypeException: The value of object \"" + data + "\" does not match the object data type " + type;
	}
	
	public static String NullNameException() {
		return "NullNameException: Objects cannot be declared without a name";
	}
	
	public static String InvalidParameterException(String invalidParameter, String function) {
	    return "InvalidParemterException: The parameter \"" + invalidParameter + "\" is not a valid parameter for the \"" + function + "\" function";
	}
	
}
