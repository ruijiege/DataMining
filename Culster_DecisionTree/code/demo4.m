function demo4(varargin)

train_data = getfield(load('dataset3_train.mat'),'data_matrix');
test_data = getfield(load('dataset3_test.mat'),'data_matrix');
categorical_column_label = getfield(load('categorical_indicator3.mat'),'categorical_column_label');

for i = 1:3
    Y = test_data(:,size(test_data,2));
    switch i
        case 1
            Y_hat = nearest_neighbor(train_data, test_data, categorical_column_label, varargin);
        case 2
            Y_hat = naive_baysian(train_data, test_data, categorical_column_label);
        case 3
            Y_hat = random_forest(train_data, test_data, categorical_column_label, varargin);           
        otherwise
            error('invalid classifier index.');
    end

    a = 0;
    b = 0;
    c = 0;
    d = 0;

    for i=1:length(Y)
        if Y(i) == 1 && Y_hat(i) == 1
            a = a+1;
        elseif Y(i) == 1 && Y_hat(i) == 0
            b = b+1;
        elseif Y(i) == 0 && Y_hat(i) == 1
            c = c+1;
        else
            d = d+1;
        end
    end

    accuracy = (a+d)/(a+b+c+d)
    precision = a/(a+c)
    recall = a/(a+b)
    f_measure = 2*a/(2*a+b+c) 

    end
end