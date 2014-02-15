function Y_hat = decision_tree(train_data, test_data, categorical_column_label, varargin)
%% varargin
%%  crtiterion_indicator: 0 -> entropy
%%                        1 -> gini
%%  prune_thredshold: when to stop growing

crtiterion_indicator = 0;
prune_thredshold = 0;

if length(varargin{1,1}) == 1
    crtiterion_indicator = varargin{1,1}{1};
elseif length(varargin{1,1}) == 2
    crtiterion_indicator = varargin{1,1}{1};
    prune_thredshold = varargin{1,1}{2};
elseif length(varargin{1,1}) > 2
    error('too many args for nearest neighbor');
end


n_test = size(test_data,1);
n_train = size(train_data,1);
d = size(train_data,2)-1;
train_X = train_data(:,1:d);
train_Y = train_data(:,d+1);
Y_hat = zeros(n_test,1);

%% build model
split_table = {};
n_leaves = 1;
n_pure_leaves = 0;
current_node_id = 1;
split_table{1}{1} = train_data;
node_cnt = 1;


while n_pure_leaves < n_leaves
    new_impurity_attributes = zeros(1,d);
    attributes_splitor = {};
    current_data = split_table{current_node_id}{1};
    current_X = current_data(:,1:d);
    current_Y = current_data(:,d+1);
    %msg = sprintf('current node ID: %d, size %d',current_node_id,length(current_Y));
    %disp(msg)
    for i=1:d
        if categorical_column_label(i) == 0
            % continuous
            combine = [current_X(:,i) current_Y];
            [~, index] = sort(combine(:,1));
            sorted_combine = combine(index,:);
            
            n_sample_in_current_node = size(combine,1);
            
            unique_value = unique(sorted_combine(:,1));
            new_impurity_split = zeros(1,length(unique_value));
            for j=1:length(unique_value)
                if j == 1
                    split1 = [];
                    split2 = sorted_combine(:,2);
                    new_impurity_split(j) =  ...
                    (length(split2)/length(sorted_combine(:,2)))*impurity(split2,crtiterion_indicator);
                else
                    split1 = sorted_combine(find(sorted_combine(:,1)<unique_value(j)),2);
                    split2 = sorted_combine(find(sorted_combine(:,1)>=unique_value(j)),2);
                    new_impurity_split(j) =  ...
                    (length(split1)/length(sorted_combine(:,2)))*impurity(split1,crtiterion_indicator) + ...
                    (length(split2)/length(sorted_combine(:,2)))*impurity(split2,crtiterion_indicator);
                end
            end
            [new_impurity_attributes(i), splitor_index] = min(new_impurity_split);
            
            if splitor_index == 1
                attributes_splitor{i} = 0;
            else
                attributes_splitor{i} = (unique_value(splitor_index)+unique_value(splitor_index-1))/2;

            end
        else
            % nomial
            combine = [current_X(:,i) current_Y];
            all_nomial = unique(current_X(:,i));
            comb_cnt = 1;
            all_comb = {all_nomial'};
            impurity_comb = [impurity(current_Y, crtiterion_indicator)];
            for j = 1:length(all_nomial)-1
                sub_comb = combnk(all_nomial',j);
                for k = 1:size(sub_comb, 1)
                    comb_cnt = comb_cnt + 1;
                    all_comb{comb_cnt} = sub_comb(k,:);
                end
            end
            for j=1:size(all_comb,2)
                if j == 1
                    split1 = [];
                    split2 = combine(:,2);
                    impurity_comb(j) = ...
                    (length(split2)/length(combine(:,2)))*impurity(split2,crtiterion_indicator);
                else
                    % calculate impurity
                    split1 = combine(find(ismember(current_X(:,i), all_comb{comb_cnt})==1) ,2);
                    split2 = combine(find(ismember(current_X(:,i), all_comb{comb_cnt})==0) ,2);
                    impurity_comb(j) = ...
                    (length(split1)/length(combine(:,2)))*impurity(split1,crtiterion_indicator) + ...
                    (length(split2)/length(combine(:,2)))*impurity(split2,crtiterion_indicator);
                end
            end
            [new_impurity_attributes(i), splitor_index] = min(impurity_comb);
            attributes_splitor{i} = all_comb{splitor_index};
        end
    end
    [impurity_after_split, attributes_index] = min(new_impurity_attributes);
    
    
    current_impurity = impurity(current_Y, crtiterion_indicator);
    %msg = sprintf('current_impurity: %f, impurity_after_split %f',current_impurity,impurity_after_split);
    %disp(msg)
    if (current_impurity - impurity_after_split - prune_thredshold) < eps
        n_pure_leaves = n_pure_leaves + 1;
        %disp('pure')
    else
        % build split
        n_leaves = n_leaves + 1;
        
        split_table{current_node_id}{2} = node_cnt + 1;    % split node 1
        if categorical_column_label(attributes_index) == 1
            filtered_data1 = current_data(find(ismember(current_data(:,attributes_index),attributes_splitor{attributes_index}) == 1),:);
        else
            filtered_data1 = current_data(find(current_data(:,attributes_index) < attributes_splitor{attributes_index}),:);
        end
        split_table{node_cnt+1}{1} = filtered_data1;
        node_cnt = node_cnt + 1;
        s1 = size(filtered_data1,1);
        %msg = sprintf('break into: %d, size: %d',node_cnt,size(filtered_data1,1));
        %disp(msg)
        
        split_table{current_node_id}{3} = node_cnt + 1;    % split node 2
        if categorical_column_label(attributes_index) == 1
            filtered_data2 = current_data(find(ismember(current_data(:,attributes_index),attributes_splitor{attributes_index}) == 0),:);
        else
            filtered_data2 = current_data(find(current_data(:,attributes_index) >= attributes_splitor{attributes_index}),:);
        end
        split_table{node_cnt+1}{1} = filtered_data2;
        node_cnt = node_cnt + 1;
        s2 = size(filtered_data2,1);
        %msg = sprintf('and %d, size %d',node_cnt,size(filtered_data2,1));
        %disp(msg)
    
        split_table{current_node_id}{4} = attributes_index;% split attribute index
        %msg = sprintf('attribute index %d', attributes_index);
        %disp(msg)
        split_table{current_node_id}{5} = attributes_splitor{attributes_index};% split attribute value
        %disp('attribute value')
        %disp(attributes_splitor{attributes_index})
        if s1 + s2 ~= length(current_Y)
            error('split error');
        end
    end
    
    current_node_id = current_node_id + 1;
end

%% apply model
for i = 1:n_test
    branch_index = 1;
    while length(split_table{branch_index}) ~= 1
        % make decision
        if categorical_column_label(split_table{branch_index}{4}) == 1
            if any(test_data() == split_table{branch_index}{5})
                branch_index = split_table{branch_index}{2};
            else
                branch_index = split_table{branch_index}{3};
            end
        else
            if test_data(i, split_table{branch_index}{4}) < split_table{branch_index}{5}
                branch_index = split_table{branch_index}{2};
            else
                branch_index = split_table{branch_index}{3};
            end
        end
    end
    
    
    if length(find(split_table{branch_index}{1}(:,d+1) == 1)) > ...
       length(find(split_table{branch_index}{1}(:,d+1) == 0))
        Y_hat(i) = 1;
    else
        Y_hat(i) = 0;
    end
end

end