function impurity_value = impurity(X, crtiterion_indicator)
%% X: input array(shoudl be binary value)

n0 = length(find(X == 0));
n1 = length(find(X == 1));
if n0+n1 ~= length(X)
    error('input array is not binary');
end
p0 = n0/(n0+n1);
p1 = n1/(n0+n1);


if crtiterion_indicator == 0
    % entropy
    if p0 == 0 || p1 == 0
        % in case log2(0) not defined
        impurity_value = 0;
    else
        impurity_value = -1*p0*log2(p0) + -1*p1*log2(p1);
    end
else
    % gini
    impurity_value = 1 - p0^2 -p1^2;
end

