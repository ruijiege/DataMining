

train_data = getfield(load('data4.mat'),'data4');
n_train = size(train_data,1);
d = size(train_data,2)-1;
train_X = (train_data(:,1:4));
train_Y = (train_data(:,5));

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
        %nomial
        current_col0 = data0(:,i);
        items0 = sort(unique(current_col0));
        probs0 = zeros(1,length(items0));
        for j=1:length(items0)
            probs0(j) = (nnz(current_col0==items0(j)))/(length(current_col0));
        end
        record0{i} = probs0;
        
        current_col1 = data1(:,i);
        items1 = sort(unique(current_col1));
        probs1 = zeros(1,length(items1));
        for j=1:length(items1)
            probs1(j) = (nnz(current_col1==items1(j)))/(length(current_col1));
        end
        record1{i} = probs1;
end

test_data = [1 3 1 1];
% sunny 1 overcast 2 rain 3
% hot 1 mild 2 cool 3 
% high 1 normal 2 low 3
% weak 1 strong 2

res0 = p0;
res1 = p1;
for j = 1:d
        res0 = res0*record0{j}(find(test_data(1,j)==sort(unique(data0(:,j)))));
        res1 = res1*record1{j}(find(test_data(1,j)==sort(unique(data1(:,j)))));
end
res0
res1