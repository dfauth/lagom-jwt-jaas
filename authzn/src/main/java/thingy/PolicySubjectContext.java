package thingy;

import java.util.function.Function;


public interface PolicySubjectContext extends Function<BasePermission, Boolean> {}
