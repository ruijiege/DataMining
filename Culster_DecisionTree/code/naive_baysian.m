function Y_hat = nearest_neighbor(train_data, test_data, categorical_column_label)



n_test = size(test_data,1);
n_train = size(train_data,1);
d = size(train_data,2)-1;
train_X = train_data(:,1:d);
train_Y = train_data(:,d+1);
Y_hat = zeros(n_test,1);


[~, index] = sort(train_Y);
sorted_train_data = train_data(index,:);
n1 = nnz(train_Y);
n0 = length(train_Y)-n1;
data0 = sorted_train_data(1:n0, :);
data1 = sorted_train_data(n0+1:end, :);
p0 = n0/n_train;
p1 = n1/n_train;

%for continuous, store normal mean and sigma
%for nomial, store probability for each item in increasing order
record0={};
record1={};
for i=1:d
    if categorical_column_label(i) == 0
        %continuous
        [mu_hat0,sigma_hat0] = normfit(data0(:,i));
        record0{i} = [mu_hat0,sigma_hat0];
        [mu_hat1,sigma_hat1] = normfit(data1(:,i));
        record1{i} = [mu_hat1,sigma_hat1];
    else
        %nomial
        current_col0 = data0(:,i);
        items0 = sort(unique(current_col0));
        probs0 = zeros(1,length(items0));
        for j=1:length(items0)
            probs0(j) = (nnz(current_col0==items0(j))+1)/(length(current_col0)+length(items0));
        end
        record0{i} = probs0;
        
        current_col1 = data1(:,i);
        items1 = sort(unique(current_col1));
        probs1 = zeros(1,length(items1));
        for j=1:length(items1)
            probs1(j) = (nnz(current_col1==items1(j))+1)/(length(current_col1)+length(items1));
        end
        record1{i} = probs1;
    end
end

for i = 1:n_test
    res0 = p0;
    res1 = p1;
    for j = 1:d
        if categorical_column_label(j) == 0
            res0 = res0*normpdf(test_data(i,j),record0{j}(1),record0{j}(2));
            res1 = res1*normpdf(test_data(i,j),record1{j}(1),record1{j}(2));
        else
            res0 = res0*record0{j}(find(test_data(i,j)==sort(unique(data0(:,j)))));
            res1 = res1*record1{j}(find(test_data(i,j)==sort(unique(data1(:,j)))));
        end
    end
    if res0>res1
        Y_hat(i) = 0;
    else
        Y_hat(i) = 1;
    end
end

    
    