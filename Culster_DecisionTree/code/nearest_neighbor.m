function Y_hat = nearest_neighbor(train_data, test_data, categorical_column_label, varargin)
%% output
%%  Y_hat: prediction of class label for test_data
%% varargin
%%  categorical_column_label: for nomial value, use different distance metric 
%%  k: number of nearest neighbor taken into condiseration
%%  weight_flag: binary(0: major voting, 1: weighted 1/d^2)


k = 1;
weight_flag = 0;

if length(varargin{1,1}) == 1
    k = varargin{1,1}{1};
elseif length(varargin{1,1}) == 2
    k = varargin{1,1}{1};
    weight_flag = varargin{1,1}{2};
elseif length(varargin{1,1}) > 2
    error('too many args for nearest neighbor');
end


n_test = size(test_data,1);
n_train = size(train_data,1);
d = size(train_data,2)-1;
train_Y = train_data(:,d+1);
Y_hat = zeros(n_test,1);
for i=1:n_test
    dist = zeros(1,n_train);
    weights = ones(1,k);
    weight_sum = zeros(1,2); % correspond to 0,1
    for j=1:n_train
        ss = 0;
        for f=1:d
            if categorical_column_label(f) == 0
                % real value
                ss = ss + (test_data(i,f) - train_data(j,f))^2;
            else
                % nomial value
                if test_data(i,f) ~= train_data(j,f)
                    ss = ss + 1;
                end
            end
        end
        dist(1,j) = sqrt(ss);
    end
    
    combine = [dist' train_Y];
    [~, index] = sort(combine(:,1));
    sorted_combine = combine(index,:);
    
    for s=1:k
        if weight_flag == 1
            weights(1,s) = 1/sorted_combine(s,1)^2;
        end
        
        if sorted_combine(s,2) == 0
            weight_sum(1,1) = weight_sum(1,1) + weights(1,s);
        else
            weight_sum(1,2) = weight_sum(1,2) + weights(1,s);
        end
    end
        
    
    if weight_sum(1,1) < weight_sum(1,2)
        Y_hat(i) = 1;
    end   
end

    
    