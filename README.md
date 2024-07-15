# AzuleLanguage
Primitive programming language I built before I really knew how programming languages worked.

This is a very odd language with very little usage, though you could probably find somehting interesting to make with it.

Created May 2021

DOCUMENTATION (also created years ago please don't judge if it's bad):

Base Functions:
	
	write - Writes either object values or Strings to the console
		Format:
			write.(Object name here)
			write."(String of text here)"
		Exceptions:
			NonExistandObjectException
			NullPointerException
	
	new - Creates a new object
		Format:
			new.(Object type here).(Object name here).(Data value here)
		Description:
			Object types include:
				Booleans (bool) - values can be true or false
				Integers (int)  - values can be any integer number, can also be int(math) to do math when creating an int. See math function to use it.
				Strings	(str)   - values can be any string of characters
			To create an object of that type, substitute the word in parentheses for the (Object type here)
		Throws:
			ObjectAlreadyExistsException
			InvalidObjectTypeException
			NullNameException
	
	func - Declares a new function
		Format:
			func.(Function Name Here)(arg0, arg1).{
			(Code here)
			...
			}
		Description:
			The code inside of the function can be called whenever you want using the call method. When called, the code inside will run.
			To declare args, use this format:
				(int::a,bool::b)
		Throws:
			ObjectAlreadyExistsException
			NullNameException

	call - Calls a function
		Format:
			call.(Function Name Here)(arg0, arg1)
		Description:
			Runs the lines of code stored inside of the function
		Throws:
			NonExistantObjectException
	
	set - Sets the value of an existing object
		Format:
			set.(Object name here).data.(Data value here)
			set.(Object name here).obj.(Object name here)
			set.(Object name here).math (See math function)
		Throws:
			NullPointerException
			NonExistantObjectException
			IncorrectDataTypeException
		
	del - Deletes an object
		Format:
			del.(Object Name Here)
		Description:
			Deletes an object from the program
		Throws:
			NonExistantObjectException

	math - Used as an input for an int. Performs a basic math operation
		Format:
			math.(operation).(firstInput).(secondInput)
		Description:
			Operations include:
				Addition (add) 	      - adds the values
				Subtraction (sub)     - subtracts the values
				Multiplication (mult) - multiplies the values
				Division (div)        - divides the values
			Inputs can be two things - integers or integer objects. To state that a parameter is an object and not a int, use obj(object name)
			instead of a number
		Throws:
			IncorrectDataTypeException
			NullPointerException
			NonExistantObjectException
			InvalidParameterException

Loops:

	for - Loops a specified time
		Format:
			for.i.(How many times to repeat)
			(Code here)
			;
		Description:
			The code inside will loop for the specified amount of time
			You can replace "i" for any variable name that isnt taken. This variable will be equal to the
			number of times the for statement has looped starting at 0
		Throws:
			

Exceptions:
	UnknownInitialFunctionException - Thrown when a base function that is used doesn't exist
	NullPointerException		- Thrown when a null object is called
	NonExistantObjectException      - Thrown when an object is called but doesn't exist
	ObjectAlreadyExistsException    - Thrown when an object that already exists is attempted to be created
	InvalidObjectTypeException      - Thrown when an object is attempted to be created as a type that doesn't exist
	IncorrectDataTypeException      - Thrown when an object's value is set to a different data type than the object's data type (when there is only a set data type for a parameter)
	NullNameException		- Thrown when an object is declared without a name
	InvalidParameterException	- Thrown when an invalid parameter is inputed into a function (when there are certain options for a parameter)
