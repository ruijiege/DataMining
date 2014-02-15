function interpretate(M, result_file)


n = size(M,1);
cluster_label = zeros(n,1);
for i = 1:n
    if nnz(M(i,:))>0
        cluster_label(find(M(i,:) ~= 0)) = i;
    end
end

cluster_mapping = zeros(n,2);
cluster_mapping(:,1) = 0:n-1;
cluster_mapping(:,2) = cluster_label;

dlmwrite(result_file, cluster_mapping, '\t');