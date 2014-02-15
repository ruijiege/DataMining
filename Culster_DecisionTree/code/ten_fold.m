function all_result = ten_fold(file_name, classifier_index, varargin)

    

    fid = fopen(file_name);
    all_samples = textscan(fid, '%s', 'delimiter', '\n');
    fclose(fid);

    

    num_rows = size(all_samples{1,1},1);
    C = strsplit(all_samples{1,1}{1},'\t');
    num_columns = size(C, 2);

    

    % 1 by d vector indicate if a column is categorical valua e
    categorical_column_label = zeros(1, num_columns);
    for i=1:num_columns
        [r,s] = str2num(C{1,i});
        if s == 0;
            categorical_column_label(i) = 1;
        end
    end
    
    % construct data matrix
    data_matrix = zeros(num_rows,  num_columns);
    for i=1:length(categorical_column_label)
        str_record{1,i} = {};
    end  
    for i=1:num_rows
        C = strsplit(all_samples{1,1}{i},'\t');
        for j=1:num_columns
            if categorical_column_label(j) == 0;
                data_matrix(i,j) = str2num(C{1,j});
            else
               if find(ismember(str_record{1,j}, C{1,j})) ~= 0
                   data_matrix(i,j) = find(ismember(str_record{1,j}, C{1,j}));
               else
                   str_record{1,j}{1, size(str_record{1,j},2)+1} = C{1,j};
                   data_matrix(i,j) = find(ismember(str_record{1,j}, C{1,j}));
               end
            end
        end
    end
    
    % nomalize
    for i=1:num_columns-1
        data_matrix(:,i) = (data_matrix(:,i) - min(data_matrix(:,i)))/(max(data_matrix(:,i) - min(data_matrix(:,i))));
    end
    
    
    all_result = zeros(4,11);
    for iter=1:10
        % permutate
        perm_order = randperm(num_rows);
        perm_data_matrix = data_matrix(perm_order,:);
        train_data = perm_data_matrix(1:9*floor(num_rows/10), :);
        test_data = perm_data_matrix(9*floor(num_rows/10)+1:end, :);
        
        
        % choose classifier
        switch classifier_index
            case 1
                Y_hat = nearest_neighbor(train_data, test_data, categorical_column_label, varargin);
            case 2
                Y_hat = decision_tree(train_data, test_data, categorical_column_label, varargin);
            case 3
                Y_hat = naive_baysian(train_data, test_data, categorical_column_label);
            case 4
                Y_hat = random_forest(train_data, test_data, categorical_column_label, varargin);           
            otherwise
                error('invalid classifier index.');
        end
        
        
        % calculate performance
        Y = test_data(:,num_columns);
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
        
        % accuracy 
        all_result(1,iter) = (a+d)/(a+b+c+d);
        % precision
        all_result(2,iter) = a/(a+c);
        % recall 
        all_result(3,iter) = a/(a+b);
        % f_measure
        all_result(4,iter) = 2*a/(2*a+b+c);               
    end   
    
   all_result(:,11) = mean(all_result(:,1:10),2);
    
end