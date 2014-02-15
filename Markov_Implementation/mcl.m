function M = mcl(input, e, r, prune_threshold, converge_threshold, show)
% INPUT:
%   input: input file name
%   e:  power parameter
%   r:  inflation parameter



if nargin < 6
    show = 0;
    if nargin < 5
        converge_threshold = 0.001;
        if nargin < 4 
            prune_threshold = 0.001;
            if nargin < 3
                r = 2;
                if nargin < 2
                    e = 2;
                    if nargin < 1
                        error('no input file');
                    end
                end
            end
        end
    end
end


%% load input file and construct matrix
pre_links = load(input);
num_links = length(pre_links);
% give each point a mapping ID
points = unique(pre_links);
num_points = length(points);

M = zeros(num_points);
for i = 1:num_links
    a = pre_links(i,1);
    b = pre_links(i,2);
    a_index = find(points == a);
    b_index = find(points == b);
    M(a_index, b_index) = 1;
    M(b_index, a_index) = 1;
end
M = M + eye(num_points);

if show == 1
    pause(1);
    visualize(M);
end

% normalize
for i = 1:num_points
    M(:,i) = M(:,i)/sum(M(:,i));
end

%% loops
old_M = zeros(size(M));
cnt = 0;
while(~isempty(find(abs(M - old_M) > converge_threshold, 1)))
% while(~isequal(old_M,M))
    old_M = M;
    M = M^e;
    M = M.^r;
    for i = 1:num_points
        M(:,i) = M(:,i)/sum(M(:,i));
    end
    
    % prune
    temp = M;
    temp( M < prune_threshold) = 0;
    M = temp;
    
    %disp(M)
    if show == 1
        pause(1);
        visualize(M);
    end
    
    cnt = cnt + 1;
end

%% 

end