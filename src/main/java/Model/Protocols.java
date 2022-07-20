package Model;

public class Protocols {
    public static void nullPointerCheck(Object originObject, Object toCheck ,String toCheckType)throws NullPointerException{
        if(toCheck == null) {
            throw new NullPointerException("Null pointer at: " + originObject.getClass().getSimpleName() + ", " + toCheckType + " is null");
        }
    }
}
